
(ns product-watchlist.core
  (:require [com.stuartsierra.component :as component]
            [product-watchlist.system :as system])
  (:gen-class))

(defn -main [& args]
  (let [config {}  ; Add configuration loading here if needed
        system (-> (system/new-system config)
                   (component/start-system))]
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(component/stop-system system)))
    (println "System started...")
    @(promise)))  ; Keep the system running