(defproject slack-commands "0.1.0-SNAPSHOT"
  :description "Various Slack slash commands"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.trace "0.7.11"]
                 [environ "1.1.0"]
                 [compojure "1.6.2"]
                 [ring/ring-defaults "0.3.3"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [ring/ring-json "0.5.1"]
                 [clojure.java-time "0.3.3"]
                 [clj-http "3.12.3"]
                 [cheshire "5.10.2"]
                 [clj-spotify "0.1.9"]
                 [http-kit "2.5.3"]
                 [net.mikera/imagez "0.12.0"]]
  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.1.0"]]
  :ring {:handler slack-commands.handler/app}
  :uberjar-name "slack-commands.jar"
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}
   :uberjar {:aot :all
             :main slack-commands.server}})
