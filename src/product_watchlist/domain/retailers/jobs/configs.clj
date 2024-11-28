(ns product-watchlist.domain.retailers.jobs.configs)

(defn incredible-connection [url]
  {:job
   {:id "incredible-connection"
    :links [url]
    :entities
    {:product-card
     {:root-selector ".product-item"
      :fields
      {:price {:selector "[data-price-type=finalPrice]" :type "inner-text"}
       :previous-price {:selector "[data-price-type=oldPrice]" :type "inner-text"}
       :title {:selector ".product-item-name" :type "inner-text"}
       :product-url {:selector ".product-item-link" :type "attribute" :attr "href"}}}
     :product-screen
     {:root-selector "#maincontent"
      :fields
      {:price {:selector ".product-info-row-wrapper [data-price-type=finalPrice]" :type "inner-text"}
       :previous-price {:selector ".product-info-row-wrapper [data-price-type=oldPrice]" :type "inner-text"}
       :title {:selector ".product-info-row-wrapper .page-title" :type "inner-text"}
       :url {:type "current-url"}
       :sku {:selector ".product-info-row-wrapper .sku .value" :type "inner-text"}
       :overview {:selector ".product-info-row-wrapper .attribute.overview li" :type "all-matching-text"}
       :images {:selector ".product-info-row-wrapper img.gallery--image" :type "attributes-list" :attr "src"}
       :attributes {:selector "tr.attribute-row"
                    :type "dynamic-field-value-map"
                    :field-selector "th[scope=row].label"
                    :value-selector "td.data"}}}}
    :actions
    [{:type "extract-entity" :entity "product-card"}
     {:type "extract-entity" :entity "product-screen"}]
    :sink {:sink-type "file" :directory "test-dir"}}})