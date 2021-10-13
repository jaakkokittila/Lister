(ns lister.views
  (:require
   [re-frame.core :as re]
   [lister.subs :as subs]
   [lister.events :as events]))

(defn product-item [product]
  (let [id (:product product)
        checked (:checked product)
        amount (:amount product)
        products @(re/subscribe [::subs/products])
        categories @(re/subscribe [::subs/categories])
        filtered-product (first (filterv #(= id (:id %)) products))
        product-name (:name filtered-product)
        category (first (filterv #(= (:category filtered-product) (:id %)) categories))
        category-name (:name category)]
    [:div.product
     [:div.name-and-category
      [:p.product-name product-name]
      [:p.category-name category-name]]
     [:p.product-amount.bluetext.blueborder amount]]))

(defn list-view []
  (let [active-list @(re/subscribe [::subs/active-list])
        lists @(re/subscribe [::subs/lists])
        active-list-contents (first (filterv #(= (:id %) active-list) lists))
        product-list (doall (for [product (:products active-list-contents)]
                              (product-item product)))]
    [:div#list-view
     [:div#list-view-top
      [:p#back-button.bluetext.blueborder
       {:onClick #(re/dispatch [::events/set-active-list nil])}
       "<-"]
      [:p#list-name.bluetext (:name active-list-contents)]
      [:p#edit-button.bluetext.blueborder "Edit"]]
     [:div#product-list product-list]]))

(defn list-list [list]
  (let [return-list (doall
                     (for [l list]
                       ^{:key (:id l)}
                       [:div.list.blueborder
                        {:onClick #(re/dispatch [::events/set-active-list (:id l)])}
                        [:div.list-name (:name l)]]))]
    return-list))

(defn list-component []
  (let [lists @(re/subscribe [::subs/lists])
        pinned-lists (filterv #(= (:pinned %) true) lists)
        unpinned-lists (filterv #(= (:pinned %) false) lists)
        pinned-list (list-list pinned-lists)
        unpinned-list (list-list unpinned-lists)]
    [:div#list-component
     (when (> (count pinned-lists) 0)
       [:p.bluetext "Pinned"])
     [:div.list-list pinned-list]
     [:div.list-list unpinned-list]]))

(defn main-panel []
  (let [active-list @(re/subscribe [::subs/active-list])
        active-component (if (nil? active-list)
                           [list-component]
                           [list-view])]
    [:div#container
     [:h1#title.bluetext "Shopper"]
     (when-not active-list
       [:p#plus.bluetext.blueborder "+"])
     active-component]))
