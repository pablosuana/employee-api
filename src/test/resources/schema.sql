CREATE TABLE if not exists "test_table" (
    "id" varchar(50) NOT NULL,
    "full_name" varchar(100) NOT NULL,
    "email" varchar(300) NOT NULL,
    "date_of_birth" varchar(10) NOT NULL,
    "hobbies" varchar(600),
    "created_at" timestamp NOT NULL DEFAULT now(),
    PRIMARY KEY ("email", "created_at")
    );