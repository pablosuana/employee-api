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

### Credentials

As stated in the requirements, the creeate, update and delete methods do require