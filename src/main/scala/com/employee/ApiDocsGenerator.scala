package com.employee

import com.employee.HttpApplication.repository
import com.employee.infrastructure.endpoints.{CreateEmployeeEndpoint, DeleteEmployeeEndpoint, GetAllEmployeesEndpoint, GetEmployeeEndpoint, UpdateEmployeeEndpoint}
import sttp.apispec.Tag
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.apispec.openapi.{Info, OpenAPI, Server}
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

import java.nio.file.{Files, Paths, StandardOpenOption}

// To execute it, write object ApiDocsGenerator extends App and run it manually
object ApiDocsGenerator {

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

  Files.write(Paths.get("api-specs.yaml"), yml.getBytes, StandardOpenOption.CREATE)

}
