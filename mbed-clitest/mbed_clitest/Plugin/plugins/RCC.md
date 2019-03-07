# RCC client Plugin for mbed-clitest
This plugin is a client application for accessing a RCC server that communicates with a device server via connector/cloud.

## Dependencies
The extapp has one depency outside of the dependencies of clitest: mdsrestclient. This is available from a private repository in github:
https://github.com/ARMmbed/mds-rest-testing

# Structure
The plugin has been implemented in RCC.py, which contains The RCCClient class and all the code of the application.
The RCCClient class is the class that contains the functionalities of the client application.

# RCCClient
RCCClient is the class that implements the functionalities of the client application.
It contains both initialization and configuration methods as well as helper methods for automating functionalities used by testcases.

## Method prototypes
Public initialization and configuration methods:

* initialize(filename=None, filepath=None, suite=None)
    * initializes the client with configuration found in filepath/filename, sets default rcc URI:s to config and creates TestUtils instance.
    * returns True or None.
* set_uri(name, uri)
    * sets uri uri for rcc server endpoint name.
* readconfigs(filename=None, filepath=None, suite=None)
    * Reads configuration from filepath/filename. defaults to mbed_clitest/ExtApps/RCC_config/config.json.
* setconfigs(configs)
    * sets configuration dictionary to a custom value.
* setter functions for prenamed configurations (rcc_address, rcc_port, apikey, cloud_address)

Helper functions for testcases:

* init_app_and_register(filepath=None, filename=None, cb_url=None, target_ep=None, apikey=None, cloud=None, rccport=None, rccaddr=None, username=None, passwd=None)
    * initializes the client using initialize (see above), replaces read configurations with provided ones if needed, creates an application on the rcc server, registers a callback url cb_url to cloud server ep target_ep.
    * if parameters are None, defaults are used.
* create_app(rcc_addr=None, rcc_port=None, username=None, passwd=None)
    * creates an application on an rcc server at rcc_addr rcc_port. If None, will use AWS. If username and passwd provided, will use basic authentication. Otherwise an access token is used from configuration.
* register_callback(url=None, target_ep=None, username=None, passwd=None)
    * registers a callback url url using cloud server endpoint target_ep. If parameters not provided, uses defaults.
* delete_callback()
    * Deletes the callback registration for this app. Returns mdsrestclient.CallbackDeletionResult or None.
* clearCallbacks()
    * clears received callbacks/notifications from the app created for the client.
* delete_app(delete_cb=True)
    * deletes the application from the RCC server. Param delete_cb is used to determine
    if callback registration should be deleted before deleting app. Defaults to True.
* wait_for_<cb_type>_callback(timeout=20)
    * waits for a callback of type cb_type (registration, registration update, notification, unregistration) for timeout seconds.
* timed_device_addess(device, resource, method="GET", sync=False, value=None, content_type=None, timeout=20)
    * performs a device access on cloud mds.
* wait_for_async_callback(timeout=20, async_id=None):
    * waits for an asynchronous callback from mds for timeout seconds. If async_id specified, looks for that specific id.
* send_rest_command_and_wait_for_async_callback(endpoint_name, resource_uri, method="GET", value=None, content_type=None access_timeout=20, cb_timeout=20)
    * Performs a device access and then waits for an asynchronous callback that was spawned by this access.
* get_access_result(initial_result)
    * Returns mds response from initial access result object, if there is no asynchronous id in the object. If the async_id exists, None is returned.
* getpayload(notif)
    * Gets and decodes the base64 encoded payload from a notification dictionary. Format must be {..., "payload": <payload to be decoded>}
* app_send_pre_subscribe_to_resource(endpoint_name, resource_uri)
    * App sends a subscription request for resource_uri to mds. Still experimental.
* remove_pre_subscriptions(endpoint_name)
    * App asks mds to remove subscriptions for endpoint
* app_sends_subscribe_to_resource(endpoint_name, resource_uri)
    * App sends subscription for resource to mds.
* app_send_subscribe_to_object(endpoint_name, object_uri, content_type=None)
    * App sends subscription for object to mds.
* app_sends_cancel_to_resource(endpoint_name, resource_uri=None, content_type=None)
    * App cancels subscription for resource or endpoint if resource_uri is None.
* app_removes_all_subscriptions()
    * App sends a DELETE to the mds and removes all subscriptions.
* get_active_endpoints()
    * App checks mds for active endpoints it has access to.
* get_endpoint_resources(endpoint)
    * App polls endpoint for resources it has available.

## Example use in a test case
Example of use in test case is available in RCC.py
