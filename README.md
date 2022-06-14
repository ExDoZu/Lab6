# Laboratory work №6

### Enter the following commands to build and run the project:

```shell
mvn package
```
Executable JARs are packed to the directory 'executable'.
```shell
java -jar .\executable\server6.jar <port>
java -jar .\executable\client6.jar <filePath> <port>
```

###### \<filePath> - you need to specify the path to the data file. \<port> is an optional argument, 52300 - default

### Example of a valid data file:

`data.json`
```json
{
  "organizations": [
    {
      "key": "key1",
      "postalAddress": null,
      "coordinates": {
        "x": 923,
        "y": 4.0
      },
      "name": "name2",
      "annualTurnover": null,
      "id": 2,
      "creationDate": 7654536,
      "type": "PUBLIC"
    },
    {
      "key": "bubblegum",
      "postalAddress": "address",
      "coordinates": {
        "x": 2,
        "y": 5.4
      },
      "name": "name 1",
      "annualTurnover": 2.3,
      "id": 1,
      "creationDate": 97658765,
      "type": "COMMERCIAL"
    }
  ]
}
```
