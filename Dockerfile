# Use an OpenJDK base image with version 11
FROM openjdk:11

# Install curl, gnupg, and other dependencies
RUN apt-get update && \
    apt-get install -y curl gnupg2 && \
    rm -rf /var/lib/apt/lists/*

# Add the sbt repository and public key
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --dearmor | tee /usr/share/keyrings/sbt-archive-keyring.gpg > /dev/null

# Import the GPG key
RUN gpg --no-default-keyring --keyring /usr/share/keyrings/sbt-archive-keyring.gpg --import /usr/share/keyrings/sbt-archive-keyring.gpg

# Install sbt
RUN echo "deb [signed-by=/usr/share/keyrings/sbt-archive-keyring.gpg] https://repo.scala-sbt.org/scalasbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-get update && \
    apt-get install -y sbt

# Set the working directory inside the container
WORKDIR /app

# Copy the project files to the container
COPY . /app

# Debugging: List the contents of /app
RUN ls -al /app

# Build the Scala project using SBT
RUN sbt clean compile

ENTRYPOINT ["top", "-b"]

# Expose the port that your Scala application will run on (adjust as needed)
EXPOSE 8080

# Run the Scala application using SBT (adjust as needed)
CMD ["sbt", "run"]
