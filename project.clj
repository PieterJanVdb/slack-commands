(defproject slack-commands "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.trace "0.7.10"]
                 [environ "1.1.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [ring/ring-json "0.5.0"]
                 [clj-time "0.15.2"]
                 [clj-http "3.12.1"]
                 [cheshire "5.10.0"]
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
