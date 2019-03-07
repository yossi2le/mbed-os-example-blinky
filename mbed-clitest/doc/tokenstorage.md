# Token storage
Tokenstorage is a helper class for test case developers who need to work with tokens for REST services etc. 
It allows the user to save and fetch single and multiple tokens from files using id:s and regular expressions. 

## Structure
This module is built as an extension to clitest. It is split into two files:

* a wrapper for the extension in [FileApi.py](../mbed_clitest/Extensions/FileApi.py)
* The logic in [SessionFiles.py](../mbed_clitest/Extensions/file/SessionFiles.py)

## TokenStorage
The primary interface of the storage is the TokenStorage class. It contains the following methods:

* saveToken(id, token, life_time, creation_time, userRole=None)
* getTokens(filename=None, filepath=None, id=None, regex=False, userRole=None, expired=None)
* deleteFile(filename=None, filepath=None)
* tagFile(tag=None)

These methods can be accessed from testcase by creating the interface object with 

* self.TestCaseTokenStorage(filename=None, filepath=None, logger=None)

Tokens are retrieved from the storage as Token objects that have multiple getter and setter methods as well as a helper to convert the token to a dictionary.

Token objects have the following methods:

* get(key)
* get_token()
* get_user_role()
* get_lifetime()
* get_creationtime()
* getRemainingLife()

GetRemainingLife returns the remaining life of the token in seconds. Internally it uses time.time().
The other methods are just getters for prenamed fields in the object. They use the get(key) function to get the value.


### Inside the Storage
TokenStorage uses JsonFile class (also in [SessionFiles.py](../mbed_clitest/Extensions/file/SessionFiles.py)) to save the data in JSON format.
It has no support for locking files at the moment so beware of multi-threading and multi-processing if you intend to use this extension. 

## Logger
The API uses a default, barebones console logger unless you provide it with a custom logger. 
The logger name you can look out for is SessionFiles. 
The bench logger is used as default when creating the interface from testcase with self.TestCaseTokenStorage

## Example use in a test case
These methods can be accessed from testcase by creating the interface object with:
 
* ts = self.TestCaseTokenStorage(filename=None, filepath=None, logger=None)

After this you have access to the methods provided by TokenStorage. 



