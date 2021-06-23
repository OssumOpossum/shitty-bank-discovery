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

## Second Increment/Package

The basic functionality is there, the customer is satisfied - but has a new set of requirements regarding resilience, traceability as well as functionality.

### Dead letter topics

The management is annoyed by the fact that there is random noise in the message topics and issued a CR to keep those messages in a separate topic for manual inspection later on.

So from now on: whenever a message does not conform to the expected data model it is expected that this message will be republished as a `DeadLetterMessage` in the configured dead-letter.topic

### Message de-duplication

Due to a bug in their corporate IT systems it is possible that several transactions with the same ID will be produced. This is obviously bad - so we're instructed to fix this issue on our end. The rule is as follows:

If a transaction is issued with a `transactionId` that is already known, the message will be:

* ignored if it completely equals the known transaction
* published as a `DeadLetterMessage` for manual inspection else (in the dead-letter.topic)

### Bank Statements

Customers have the possibility to request a printed bank statement for all transactions on record. SHIT™ has become - thanks to our software - so successful over the last couple of months, that it is no longer feasible to query&print these statements synchronously. That's why we need a topic&message for those bank statements.

Our BankingService was extended by the method `publishBankStatement(String accountNumber)` - this method has to fetch all bank statements for the given account and publish a message to the `banking.statement` topic.

The customer was so satisfied with our code that we are now also entrusted with the design and creation of all verification tests as well as the message data-model! The customer obviously expects nothing less than 100% test coverage and all corner cases and eventualities have to be taken into account.

The published message object has to contain all information to generate the following bank statement:

| Customer: (GEORGE BUSH) #900002 | Credit line: 500           |  Balance: 200   |
| ------------- |:-------------:| -----:|
| RECEIVED      | 400 | from 900004 (ANDREAS HERZOG) |
| SENT      | 200 | to 900013 (ANONYMOUS) |

(If account owner names are unknown "ANONYMOUS" will be set).