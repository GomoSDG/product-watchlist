(ns user
  (:require #_[clojure.tools.namespace.repl :refer [refresh]]
   [com.stuartsierra.component :as component]
            [product-watchlist.system :as system]
            [product-watchlist.domain.migrations.core :as migrations]
            [migratus.core :as migratus]))

(def system nil)

(defn create-migration [domain-name name]
  (migrations/create domain-name name))

(defn migrate []
  (migrations/migrate))

(defn rollback []
  (migrations/rollback))

(defn init []
  (migrations/init))

(defn start []
  (alter-var-root #'system
                  (fn [s] (if s s (system/new-system {}))))
  :started)

(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s))))
  :stopped)

#_(defn reset []
    (stop)
    (refresh :after 'user/start))

(comment
  ;; Development helpers - evaluate these in the REPL
  (start)  ; Start the system
  (stop)   ; Stop the system
  #_(reset)  ; Reset the system

  ;; Migration helpers
  (create-migration "product" "create-product-table") ; Create a new migration
  (migrate)                                ; Run pending migrations
  (rollback)                              ; Rollback last migration
  (init)                                  ; Initialize migration infrastructure
  )