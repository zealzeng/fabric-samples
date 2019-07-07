package main


import (

	"bytes"

	"encoding/json"

	"fmt"




	"github.com/hyperledger/fabric/core/chaincode/shim"

	sc "github.com/hyperledger/fabric/protos/peer"

)


// Define the Smart Contract structure

type SmartContract struct {

}


// Define the car structure, with 4 properties.  Structure tags are used by encoding/json library

type Student struct {

	Id   string `json:"id"`

	Name  string `json:"name"`

	Mark string `json:"mark"`

	Record  string `json:"record"`

}


/*

 * The Init method is called when the Smart Contract "fabcar" is instantiated by the blockchain network

 * Best practice is to have any Ledger initialization in separate function -- see initLedger()

 */

func (s *SmartContract) Init(APIstub shim.ChaincodeStubInterface) sc.Response {

	return shim.Success(nil)

}


func (s *SmartContract) Invoke(APIstub shim.ChaincodeStubInterface) sc.Response {


	// Retrieve the requested Smart Contract function and arguments

	function, args := APIstub.GetFunctionAndParameters()

	// Route to the appropriate handler function to interact with the ledger appropriately

	if function == "queryStudent" {

		return s.queryStudent(APIstub, args)

	} else if function == "initLedger" {

		return s.initLedger(APIstub)

	} else if function == "create" {

		return s.create(APIstub, args)

	} else if function == "queryAll" {

		return s.queryAll(APIstub)

	}


	return shim.Error("Invalid Smart Contract function name.")

}



func (s *SmartContract) initLedger(APIstub shim.ChaincodeStubInterface) sc.Response {



	student1 := Student{Id: "18180100001", Name: "Peter", Mark: "97", Record: "Play Computer"}

	student2 := Student{Id: "18180100002", Name: "Tom", Mark: "88", Record: "Sing, Dance, Rap, Play Basketball"}

	student3 := Student{Id: "18180100003", Name: "Bob", Mark: "64", Record: "Fuck, Gay"}

	//student4 := Student{id: "18180100004", name: "Andy", mark: "72", record: "Cheat in the exam, Copy"}

	//student5 string = "Fuck you the bug"

	Bytes1, _ := json.Marshal(student1)


	APIstub.PutState("STUDENT1", []byte(Bytes1))

	Bytes2, _ := json.Marshal(student2)


	APIstub.PutState("STUDENT2", []byte(Bytes2))

	Bytes3, _ := json.Marshal(student3)


	APIstub.PutState("STUDENT3", []byte(Bytes3))



	return shim.Success(nil)

}


func (s *SmartContract) queryStudent(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {


	if len(args) != 1 {

		return shim.Error("Incorrect number of arguments. Expecting 1")

	}


	Bytes, _ := APIstub.GetState(args[0])

	jsonResp := "{\"Name\":\"" + args[0] + "\",\"Information\":\"" + string(Bytes) + "\"}"

	fmt.Printf("Query Response:%s\n", jsonResp)

	return shim.Success(Bytes)

}

func (s *SmartContract) create(APIstub shim.ChaincodeStubInterface, args []string) sc.Response {


	if len(args) != 5 {

		return shim.Error("Incorrect number of arguments. Expecting 5")

	}


	//var car = Student{id: args[1], name: args[2], mark: args[3], record: args[4] }


	//Bytes, _ := json.Marshal(car)

	//APIstub.PutState(args[0], AsBytes)


	return shim.Success(nil)

}


func (s *SmartContract) queryAll(APIstub shim.ChaincodeStubInterface) sc.Response {


	startKey := "STUDENT0"

	endKey := "STUDENT999"


	resultsIterator, err := APIstub.GetStateByRange(startKey, endKey)

	if err != nil {

		return shim.Error(err.Error())

	}

	defer resultsIterator.Close()


	// buffer is a JSON array containing QueryResults

	var buffer bytes.Buffer

	buffer.WriteString("[")


	bArrayMemberAlreadyWritten := false

	for resultsIterator.HasNext() {

		queryResponse, err := resultsIterator.Next()

		if err != nil {

			return shim.Error(err.Error())

		}

		// Add a comma before array members, suppress it for the first array member

		if bArrayMemberAlreadyWritten == true {

			buffer.WriteString(",")

		}

		buffer.WriteString("{\"Key\":")

		buffer.WriteString("\"")

		buffer.WriteString(queryResponse.Key)

		buffer.WriteString("\"")


		buffer.WriteString(", \"Record\":")

		// Record is a JSON object, so we write as-is

		buffer.WriteString(string(queryResponse.Value))

		buffer.WriteString("}")

		bArrayMemberAlreadyWritten = true

	}

	buffer.WriteString("]")

	fmt.Printf(" queryAll:\n%s\n", buffer.String())


	return shim.Success(buffer.Bytes())

}




// The main function is only relevant in unit test mode. Only included here for completeness.

func main() {

	// Create a new Smart Contract

	err := shim.Start(new(SmartContract))

	if err != nil {

		fmt.Printf("Error creating new Smart Contract: %s", err)

	}
}