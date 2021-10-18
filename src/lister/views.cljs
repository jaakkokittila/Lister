(ns lister.views
  (:require
   [re-frame.core :as re]
   [clojure.string :as str]
   [lister.subs :as subs]
   [lister.events :as events]))

(defn new-product-view []
 (let [new-product @(re/subscribe [::subs/new-product])
       categories @(re/subscribe [::subs/categories])
       new-product-name (:name new-product)
       category (:category new-product)
       options (doall (for [category (vals categories)]
                        ^{:key (:id category)}
                        [:option {:value (:id category)} (:name category)]))]
  [:div#new-product-view
   [:h2.center-text.bluetext "Create new product"]
   [:table#new-product-form.margin-center
    [:tbody
     [:tr
      [:th [:p.bluetext "Name"]]
      [:th [:input.new-product-input.bluetext.blueborder
            {:type "text"
             :value new-product-name
             :on-change #(re/dispatch [::events/update-new-product-name (-> % .-target .-value)])}]]]
     [:tr
      [:th [:p.bluetext "Category"]]
      [:th [:select.new-product-input.bluetext.blueborder
            {:type "select"
             :value category
             :on-change #(re/dispatch [::events/update-new-product-category (-> % .-value)])}
            options]]]]]
   [:div#product-buttons.margin-center.flex-row
      [:div.big-button.bluetext.blueborder.center-text.hoverable
       {:on-click #(re/dispatch [::events/update-new-product nil])} "Cancel"]
      [:div.big-button.bluetext.blueborder.center-text.hoverable 
       {:on-click #(re/dispatch [::events/save-new-product])}"Save"]]]))

(defn product-item [product search-or-view]
  ;;search-or-view true if shopping list view false if search-view
  (let [id (if search-or-view
             (:product product)
             (:id product))
        amount (:amount product)
        products @(re/subscribe [::subs/products])
        categories @(re/subscribe [::subs/categories])
        filtered-product (get-in products [id])
        product-name (if search-or-view
                       (:name filtered-product)
                       (:name product))
        checked (:checked product)
        category (if search-or-view
                   (get-in categories [(:category filtered-product)])
                   (get-in categories [(:category product)]))
        category-name (:name category)
        style (if search-or-view
                nil
                {:max-height "250px"})]
    ^{:key id}
    [:div.product.margin-center.flex-row
     {:style style}
     [:div.name-and-category
      [:p.product-name product-name]
      [:p.category-name category-name]]
    (if search-or-view
      [:div.flex-row
       [:div.product-amount-control.bluetext.blueborder.center-text.hoverable
        {:on-click #(re/dispatch [::events/update-product-amount id true])} "+"]
       [:div.product-amount.bluetext.blueborder.center-text amount]
       [:div.product-amount-control.bluetext.blueborder.center-text.hoverable
        {:on-click #(re/dispatch [::events/update-product-amount id false])} "-"]
       [:input#checked {:type "Checkbox"
                        :checked checked
                        :on-change #(re/dispatch [::events/toggle-checked id])}]]
     [:div.product-amount-control.bluetext.blueborder.center-text
           {:on-click #(re/dispatch [::events/add-product-to-list id])} "+"])]))

(defn search-view []
  (let [search (str/lower-case @(re/subscribe [::subs/search]))
        products (vals @(re/subscribe [::subs/products]))
        filtered-products (if (= "" search)
                            products
                            (filterv #(= search (subs (str/lower-case (:name %)) 0 (count search))) products))
        product-list (doall (for [product filtered-products]
                              (product-item product false)))]
    [:div#search-view
     [:div.list-view-top.margin-center.flex-row
      [:p.back-button.bluetext.blueborder.center-text.hoverable
       {:on-click #(re/dispatch [::events/update-search nil])}
       "<-"]
      [:input#list-name.bluetext.blueborder
       {:type "text"
        :value search
        :on-change #(re/dispatch [::events/update-search (-> % .-target .-value)])}]]
     [:div#product-list.margin-center product-list]
     [:div#new-product-button.bluetext.blueborder.margin-center.center-text.hoverable
      {:on-click #(re/dispatch [::events/init-new-product])}
      "Add new product"]]))

(defn list-view []
  (let [active-list @(re/subscribe [::subs/active-list])
        lists @(re/subscribe [::subs/lists])
        active-list-contents (get-in lists [active-list])
        pin-text (if (:pinned active-list-contents)
                   "Unpin"
                   "Pin")
        product-list (doall (for [product (vals (:products active-list-contents))]
                              (product-item product true)))]
    [:div#list-view
     [:div.list-view-top.margin-center.flex-row
      [:p.back-button.bluetext.blueborder.center-text.hoverable
       {:on-click #(re/dispatch [::events/set-active-list nil])}
       "<-"]
      [:input#list-name.bluetext.blueborder
       {:type "text"
        :value (:name active-list-contents)
        :on-change #(re/dispatch [::events/update-list-name (-> % .-target .-value)])}]]
     [:div#product-list.margin-center product-list]
     [:div#list-controls.flex-row
      [:div#pin-button.bluetext.blueborder.center-text.hoverable
       {:on-click #(re/dispatch [::events/toggle-pinned (:id active-list-contents)])}
       pin-text]
      [:div.plus.bluetext.blueborder.center-text.hoverable {:on-click #(re/dispatch [::events/update-search ""])} "+"]]]))

(defn list-list [list]
  (let [return-list (doall
                     (for [l list]
                       ^{:key (:id l)}
                       [:div.list.blueborder.center-text.hoverable
                        {:onClick #(re/dispatch [::events/set-active-list (:id l)])}
                        [:div.list-name (:name l)]]))]
    return-list))

(defn list-component []
  (let [lists @(re/subscribe [::subs/lists])
        pinned-lists (filterv #(= (:pinned %) true) (vals lists))
        unpinned-lists (filterv #(= (:pinned %) false) (vals lists))
        pinned-list (list-list pinned-lists)
        unpinned-list (list-list unpinned-lists)]
    [:div#list-component.margin-center
     (when (> (count pinned-lists) 0)
       [:p.bluetext "Pinned"])
     [:div.list-list.flex-row pinned-list]
     [:div.list-list.flex-row unpinned-list]]))

(defn main-panel []
  (let [active-list @(re/subscribe [::subs/active-list])
        search @(re/subscribe [::subs/search])
        new-product @(re/subscribe [::subs/new-product])
        active-component (if (nil? active-list)
                           [list-component]
                           (if (nil? search)
                             [list-view]
                             (if (nil? new-product)
                               [search-view]
                               [new-product-view])))]
    [:div#container.margin-center
     [:h1#title.bluetext.center-text "Shopper"]
     (when-not active-list
       [:div#new-product-button.bluetext.blueborder.margin-center.center-text.hoverable
        {:on-click #(re/dispatch [::events/init-new-list])}
        "New list"])
     active-component]))
