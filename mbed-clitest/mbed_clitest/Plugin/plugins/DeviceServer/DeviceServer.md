# Connector/mDS WebApp tool for clitest
The Connector/mDS WebApp tool can be used to test components through the Cloud / mDS REST API.

## Usage 

### Simple usage with Connector and single mbed Client DUT
Example testcase which reads and writes resource 13/0/1 for endpoint whose id is "myEndpointId":
```python
class Testcase(Bench):
    def __init__(self):
        Bench.__init__(self,
                       name="my_client_webapp_testcase",
                       title="Example testcase where client connects to Connector and webapp uses client",
                       status="released",
                       type="sample",
                       purpose="",
                       component=["deviceserver"],
                       requirements={
                           "duts": {
                               '*': {
                                   "count": 0
                               }
                           },
                           "external": {
                               "apps": [
                                   {"name": "Connector"}
                               ]
                           }
                       }

                       )

    def case(self):
        # Reading and writing resource values
        resource_value = self.Connector.getEndpointResource("myEndpointId", uri="/13/0/1")
        self.Connector.putEndpointResource("myEndpointId", uri="/13/0/1", payload="987")

```

## Configuring webapp
The WebApp has to be configured through the clitest env_cfg.json file. The most minimal configuration for mbed Cloud requires the address, rest_address, domain and token fields to be configured. An example minimal env_cfg.json configuration can be seen below. This configuration allows us to use Cloud REST API:
```javascript
{
    "extApps": {
        "Connector": {
            "token": "APIKEY_FROM_CLOUD",
            "address": "api.mbedcloud.com/v2",
            "rest_address": "https://api.mbedcloud.com/v2",
            "port": "5684"
        }
    }
}
```
See table [Configuration field descriptions](#configuration-field-descriptions) for a list of possible keys and their description.

### 

### Configuration field descriptions for Cloud

| Key                   | Description                                                 |
| --------------------- | ----------------------------------------------------------- |
| token                 | Authentication token for the Cloud REST API.                |
| address               | URL of the server as a string, eg. "api.mbedcloud.com".     |
| coap_port             | Optional: The CoAP port of the server as an integer, default is 5683. |
| coap_port_secure      | Optional: The secure CoAP port of the server as an integer, default is 5684 |
| rest_address          | The Cloud REST API address as a string.                     |
| rest_port             | Optional: The Cloud REST API port as an integer, default is 8080 |
| type                  | The type of the server, possible values are "mDS", "Connector" or "cloud". |
| enable_rest           | Enable the Connector REST interface, defaults to true. Set to false if not required by testcase or the REST interface is unreachable by testcase. |
| rest_timeout          | Timeout of REST requests in seconds, default value is 10 |
| rcc_address           | URL of the Rest Client Container server |
| rcc_port              | Port of the Rest Client Container server |
| use_longpoll          | Uses long polling (pull) for fetching notifications. Default is callbacks (push) |
| endpoint_rest_version | Endpoint REST API version, default is 2. |
| cloud_rest_version    | Cloud REST API version, default is 3. |
| use_dynamic_apikey    | If set to true, create new API key for REST API usage using the api key provided in token or command line parameter. The created API key will be deleted during teardown. | 

Note: When using the dynamic API key feature, (use_dynamic_apikey = true) the prefix of the created API key name can be configured in an environment variable called "CLITEST_CLOUD_APIKEY_NAME".'
For example setting CLITEST_CLOUD_APIKEY_NAME=test_api_key will result in API keys such as "test_api_key_1955378787"