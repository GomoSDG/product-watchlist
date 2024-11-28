(ns product-watchlist.domain.matching
  "Product matching domain logic.
   Handles the comparison and matching of products across different sources.
   
   Key functionality:
   - Exact matching using EAN
   - Match confidence scoring
   - Match validation rules"
  (:require [product-watchlist.domain.product :as product]))