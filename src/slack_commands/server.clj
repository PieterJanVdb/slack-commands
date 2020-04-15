(ns slack-commands.server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [slack-commands.handler :refer [app]])
  (:gen-class))

(defn -main [& args]
  (run-jetty app {:port (Integer/valueOf (or (System/getenv "PORT") "8080"))}))