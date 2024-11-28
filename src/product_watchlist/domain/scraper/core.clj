(ns product-watchlist.domain.scraper.core
  "Core namespace for handling scraping operations"
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [schema.core :as schema]
            [overtone.at-at :as at]))

;; Schemas and Specs
(def JobConfig
  {:url schema/Str
   :method (schema/enum :get :post :put :delete)
   schema/Keyword schema/Any})

(s/def ::url string?)
(s/def ::method #{:get :post :put :delete})
(s/def ::config (s/keys :req-un [::url ::method]))

;; Configuration
(def ^:private config
  {:scraper-domain (or (System/getenv "SCRAPER_DOMAIN") "http://localhost:3060")
   :job-url "/start-job"
   :max-retries 3
   :retry-delay-ms 1000
   :default-timeout 60000})

;; Validation functions
(defn- validate-request-config [config]
  (when-not (s/valid? ::config config)
    (throw (ex-info "Invalid request configuration"
                    {:error (s/explain-str ::config config)}))))

(defn- validate-job [job]
  (schema/validate JobConfig job))

;; Retry mechanism
(defn- with-retries [f max-retries delay]
  (fn [& args]
    (loop [tries 1]
      (let [result (try
                     (apply f args)
                     (catch Exception e
                       (log/warn e "Request failed, attempt" tries "of" max-retries)
                       {:error e}))]
        (if (and (:error result) (< tries max-retries))
          (do
            (Thread/sleep delay)
            (recur (inc tries)))
          result)))))

(defn make-request
  "Make an HTTP request with optional body
   config - map containing :url, :method, :headers, :timeout
   body - optional request body"
  ([config] (make-request config nil))
  ([{:keys [url method headers timeout]
     :or {method :get
          timeout (:default-timeout config)}} body]
   (validate-request-config {:url url :method method})
   ((with-retries http/request (:max-retries config) (:retry-delay-ms config))
    (cond-> {:url url
             :method method
             :headers (merge {"Content-Type" "application/json"} headers)
             :timeout timeout}
      body (assoc :body (json/encode body))))))

(defn process-response
  "Process HTTP response and handle errors
   response - HTTP response map
   opts - optional map with :response-parser function (defaults to json decode)"
  [{:keys [status body error]} & {:keys [response-parser]
                                  :or {response-parser #(json/decode % true)}}]
  (let [result (cond
                 error {:success false
                        :error (.getMessage error)}

                 (not= status 200) {:success false
                                    :error (format "HTTP Error: %d - %s" status body)}

                 :else {:success true
                        :data (response-parser body)})]
    (when-not (:success result)
      (log/error "Request failed:" (:error result)))
    result))

(defn run-job
  "Execute a scraping job
   job - map containing job configuration"
  [job]
  (try
    (validate-job job)
    (-> (make-request
         {:url (str (:scraper-domain config) (:job-url config))
          :method :post}
         job)
        (process-response
         :response-parser #(-> (json/decode % true)
                               (:result)
                               (:entities))))
    (catch Exception e
      (log/error e "Failed to run job")
      {:success false
       :error (.getMessage e)})))

(comment
  (json/encode (product-watchlist.domain.retailers.jobs.configs/incredible-connection))
  (-> (run-job (product-watchlist.domain.retailers.jobs.configs/incredible-connection
                "https://www.incredible.co.za/lg-ultragear-24-full-hd-gaming-monitor-144hz-1ms-amd-freesync"))
      (count)))