(ns product-watchlist.infrastructure.scraper
  "Product data scraping infrastructure.
   Handles fetching product data from external sources.
   
   Core features:
   - HTTP request handling
   - Response parsing
   - Rate limiting
   - Error handling for external requests"
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))