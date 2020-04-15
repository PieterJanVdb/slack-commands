FROM clojure

# Create the project and download dependencies
WORKDIR /usr/src/app
COPY project.clj .
RUN lein deps

# Copy local code to the container image
COPY . .

# Create JAR
RUN mv "$(lein ring uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar

# Run the API on container startup
CMD ["java", "-jar", "app-standalone.jar"]
