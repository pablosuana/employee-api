package com.employee.infrastructure.dto.db

case class PostgresRequest(
  id: String,
  full_name: String,
  email: String,
  date_of_birth: String,
  hobbies: String,
  timestamp: String
)

