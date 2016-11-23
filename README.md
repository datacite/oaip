# DataCite OAI-PMH Provider

[![Build Status](https://travis-ci.org/datacite/oaip.svg)](https://travis-ci.org/datacite/oaip)

This is the OAI-PMH Data Provider (OAIP) application for the DataCite central infrastructure. This app is a servlet with the main functionality of disseminating the contents of the DataCite Metadata Store using OAI-PMH.

To learn more about DataCite please visit [our website](http://www.datacite.org)

To use this service please go to [http://oai.datacite.org](http://oai.datacite.org)

## Installation

Using Docker.

```
docker run -p 8080:8080 -v ~/.m2:/root/.m2 --env-file .env.example datacite/oaip:labs
```

You can now point your browser to `http://localhost:8080` and use the application.

For a more detailed configuration, including using a lcoal Maven repository on the host, look at `docker-compose.yml` in the root folder.

## Development

Follow along via [Github Issues](https://github.com/datacite/content-resolver/issues).

### Note on Patches/Pull Requests

* Fork the project
* Write tests for your new feature or a test that reproduces a bug
* Implement your feature or make a bug fix
* Do not mess with Rakefile, version or history
* Commit, push and make a pull request. Bonus points for topical branches.

## License
**Content Resolver** is released under the [Apache 2 License](https://github.com/datacite/content-resolver/blob/master/LICENSE).
