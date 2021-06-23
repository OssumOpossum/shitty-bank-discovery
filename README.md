# Shitty Bank

We've been tasked to implement a service backend for the "Shitty Holding International Transatlantic" (abbr. SHIT™).

SHIT uses state of the art messaging technology and publishes all transactions and accounts (and account holders) via Kafka. But there are some ... issues that we need to consider.


## First Increment/Package

Your first task is to add a consumer for the topics and implement the BankingService Interface according to the method annotations. The following functional (and non-functional) requirements have to be considered:

* An account owner is optional - every account owner name is unique
* The ordering of transactions is chronological within the topic
* The ordering of the accounts and related transactions is not though - if there is a transaction for an account that does not exist (yet) the transaction has a "grace period" of 10 seconds. If after 10 seconds there is still no account associated for the account number(s) the transaction is discarded
* Sadly there is no guarantee that all messages conform to the data model - there is sometimes "noise" in the topics
* The topic names can be found in the application.properties, the domain data model resides within the banking package
* You **must not** edit/change the domain model, the tests (other than exchanging the Mock for your component) or the producers. You can add new classes at your liking though and change the contents of the `KafkaConfiguration` class.

