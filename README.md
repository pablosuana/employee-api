### Employee API
This service is intended to be used to manage a Database of Employees. Through this API, 
we would be able to create, delete, update and retrieve employees in the desired datasource.

The API architecture was designed to be aligned with the clean architecture and SOLID principles 
which makes the application reliable and easily modifiable. The way I have organised it is:

<pre><code>
|-- domain: It is the core of the application. This layer contains the business logic and domain entities
|   |-- entities: Entities represent the core business object, the EmployeeBase and its instance: Employee 
|   |-- interfaces: Interfaces define the interactions between the domain layer and the application services or the outer layers. As the DB "contracts" or the repository
|   |-- useCase: The operations that can be performed on entities and value objects: CreateEmployee, UpdateEmployee, etc
|-- infrastructure: Layer responsible for dealing with external concerns, such as databases, frameworks, libraries, and external services.
|   |-- db: Implementation of Postgres Db adapters using Hikari
|   |-- dto: Modeling the data transfer objects to be used by the application
|       |-- client: The api requests and responses models can be found here
|       |-- db: The request and response models for interact with db can be found here
|   |-- endpoints: Definition of the endpoints of the api and the logic which will be applied on each of them, using the previously defined use cases (Endpoint definition and endpoint logic should be placed in different files) 
|   |-- interfaces: Instantiating the repository with the implementation details and bringing everything together
|-- ApiDocsGenerator: Piece of code to generate api specifications
|-- ApiDocsGenerator: Initialization of the API. At the moment, the http framework used (akka http) is hardcoded here, but the code should be able to use any other, a refactor is needed
</code></pre>


For the sake of being able to test the API easily, the configuration by default is using an H2 
in memory database. This would be enough to test the behaviour of the API and connection 
details can be changed later.

### Test
For testing locally, run ```sbt test``` from terminal from the project root dir and all the tests in the test module will be run.

Versions used for developing the api, which might be needed to run the tests:
* sbt = 1.9.8
* scala = 2.13.12
* java = 11

### Build the application image
For building the docker image, from the project root dir run this: ```sbt docker:publishLocal```
This will create a docker image in your computer called: **employee-api** with version **0.1.0**

Versions used for developing the api, which might be needed to build the docker image:
* sbt = 1.9.8
* scala = 2.13.12
* java = 11
* docker

### Run the application image
For running the created image just run this command: 

<code>docker run --rm -p 8080:8080 employee-api:1.0.0</code>

This will start the application and it will start accepting requests.
The configuration used by default is ```host: 0.0.0.0``` and ```port: 8080```
To check the available methods and some examples of the expected request and response by each of them, refer to the api-specs.yaml.

### Api documentation
The api documentation in swagger format can be found in the file [api-specs.yaml](api-specs.yaml)

### Credentials

As stated in the requirements, create, update and delete methods do require authentication.
The authentication used basic and the accepted set of credentials is hardcoded, so only this pair of values is accepted:
**user password**

### Database
As mentioned before, the database used by default by the service is an in memory H2 DB.
To be able to create it in the docker container, we have to add the schema.sql and data.sql files to the distribution,
which is done in the build.sbt file here: 

<pre><code>.settings(Universal / mappings := (Universal / mappings).value :+ (file(s"${baseDirectory.value}/src/main/resources/schema.sql") -> "schema.sql") :+ (file(s"${baseDirectory.value}/src/main/resources/data.sql") -> "data.sql"))</code></pre>

and then we have to copy those files into the /tmp/ docker's directory.
That's why the database definition in application.conf looks like:
<pre><code>database = "employee_table;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_LOWER=TRUE;INIT=RUNSCRIPT FROM '/tmp/schema.sql'\\;RUNSCRIPT FROM '/tmp/data.sql'"</code></pre>

Using this configuration, it won't work in local, so for local running it has to be replaced by:
<pre><code>database = "employee_table;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE;INIT=RUNSCRIPT FROM './src/main/resources/schema.sql'\\;RUNSCRIPT FROM './src/main/resources/data.sql'"</code></pre>