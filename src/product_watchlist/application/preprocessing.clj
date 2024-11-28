(ns product-watchlist.application.preprocessing
  "Data preprocessing application service.
   Handles standardization and normalization of product data.
   
   Core functionality:
   - Text normalization
   - Price standardization
   - Brand name normalization
   - Field validation and cleaning"
  (:require [clojure.string :as str]))