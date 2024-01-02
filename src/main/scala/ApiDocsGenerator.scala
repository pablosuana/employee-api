import HttpApplication.repository
import infrastructure.endpoints._
import sttp.apispec.Tag
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.apispec.openapi.{Info, OpenAPI, Server}
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

object ApiDocsGenerator extends App {

  val info = Info(
    title = "Employee-API",
    version = "1.0.0",
    description = Option("API intended to be used for managing an employee DB")
  )

  val endpoints = Seq(
    new CreateEmployeeEndpoint(repository).endpointDefinition,
    new UpdateEmployeeEndpoint(repository).endpointDefinition,
    new GetEmployeeEndpoint(repository).endpointDefinition,
    new GetAllEmployeesEndpoint(repository).endpointDefinition,
    new DeleteEmployeeEndpoint(repository).endpointDefinition
  )

  val openApi: OpenAPI =
    OpenAPIDocsInterpreter()
      .toOpenAPI(endpoints, info)
      .servers(
        List(
          Server("localhost:8080")
        )
      )
      .tags(
        List(
          Tag(
            "Employe-API",
            Option("service endpoints")
          )
        )
      )

  val yml: String = openApi.toYaml

  println(yml)
}
