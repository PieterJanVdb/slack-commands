# slack-commands

Various different Slack slash commands

* /np [Last.FM username] - Retrieves the current track or your last scrobbled track

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

You'll need to include the following environment variables

* SLACK_SIGNING_SECRET
* LAST_FM_API_KEY
* SPOTIFY_CLIENT
* SPOTIFY_SECRET

To start the API, run (including environment variables):

    lein ring server-headless

## License

Copyright © 2020 Pieter-Jan Vandenbussche
