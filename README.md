# Twitter Social Media Curation: CPEN221 Class Project

This project is a messaging and content curation service for people to interact with content on Twitter.
We have built a time-sensitive queuing data structure and used it to implement a publish-subscribe model for data sharing. 
We've used Twitter API to access and port content into your service and made our service accessible to users by implementing a client-server paradigm.
We have also incorporated basic cryptographic schemes to ensure a degree of security.

## Getting Started
To run this project, you will need Java and Maven installed on your machine.

### Prerequisites
- Java JDK 8 or above
- Maven

### Installation
1. Clone the repository:
```bash
git clone https://github.com/NakulGD/Social-Media-Curation.git
```

2. Navigate to the project directory:
```bash
cd Social-Media-Curation
```

3. Compile and build the project:
```bash
mvn clean install
```

4. Execute the main class or use Maven to run the project:
```bash
mvn exec:java -Dexec.mainClass="pheme.PhemeService"
```

## Features
- **TimeDelayQueue:** Custom implementation of a time-sensitive queue for managing messages with unique identifiers and timestamps.
- **Security:** Basic cryptographic implementations for AES and Blowfish ciphers to secure data.
- **Twitter Integration:** Utilizes Twitter API to fetch and curate Twitter content for subscribed users.
- **Publish-Subscribe Model:** Allows users to subscribe to Twitter content and receive updates in a timely manner.

## Usage
After starting the application, users can subscribe to Twitter feeds, and the service will curate content based on the subscriptions and the parameters set for the time-sensitive queue.
