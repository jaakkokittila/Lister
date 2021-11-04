(ns lister.events
  (:require
   [re-frame.core :as re]
   [lister.helpers :as helpers]))

;; Placeholder data
(def default-db
  {:products {1 {:id 1 :name "Orange Juice" :category 1}
              2 {:id 2 :name "Rice" :category 1}
              3 {:id 3 :name "Banana" :category 1}
              4 {:id 4 :name "Tooth Brush" :category 2}
              5 {:id 5 :name "Apple" :category 1}}
   :categories {1 {:id 1 :name "Groceries"}
                2 {:id 2 :name "Cosmetics"}}
   :lists {1 {:id 1 :name "Shopping list 1" :pinned false :products {1 {:product 1 :amount 3 :checked false}
                                                                     2 {:product 2 :amount 1 :checked true}
                                                                     5 {:product 5 :amount 2 :checked false}}}
           2 {:id 2 :name "Shopping list 2" :pinned true :products {1 {:product 1 :amount 3 :checked false}
                                                                    2 {:product 2 :amount 1 :checked true}
                                                                    5 {:product 5 :amount 2 :checked false}}}
           3 {:id 3 :name "Shopping list 3" :pinned false :products {1 {:product 1 :amount 3 :checked false}
                                                                     2 {:product 2 :amount 1 :checked true}
                                                                     5 {:product 5 :amount 2 :checked false}}}
           4 {:id 4 :name "Shopping list 4" :pinned false :products {1 {:product 1 :amount 3 :checked false}
                                                                     2 {:product 2 :amount 1 :checked true}
                                                                     5 {:product 5 :amount 2 :checked false}}}}
   :search nil
   :new-product nil
   :active-list nil})

(re/reg-fx ::item-already-in-list
  (fn []
    (js/alert "Item already in list!")))

(re/reg-event-db ::set-active-list
  (fn [db [_ list-id]]
    (assoc db :active-list list-id)))

(re/reg-event-db ::update-list-name
  (fn [db [_ new-name]]
    (let [active-list-id (:active-list db)]
      (assoc-in db [:lists active-list-id :name] new-name))))

(re/reg-event-db ::remove-from-list
  (fn [db [_ product-id]]
    (let [active-list-id (:active-list db)]
     (update-in db [:lists active-list-id :products] dissoc product-id))))

(re/reg-event-fx ::update-product-amount 
  (fn [{db :db} [_ product-id operation]]
    (let [active-list-id (:active-list db)
          current-amount (get-in db [:lists active-list-id :products product-id :amount])]
      (if operation
        {:db (update-in db [:lists active-list-id :products product-id :amount] inc)}
        (if (> current-amount 1)
          {:db (update-in db [:lists active-list-id :products product-id :amount] dec)}
          {:dispatch [::remove-from-list product-id]})))))

(re/reg-event-db ::update-search
  (fn [db [_ search]]
    (assoc db :search search)))

(re/reg-event-db ::toggle-checked
  (fn [db [_ product-id]]
    (let [active-list-id (:active-list db)]
      (update-in db [:lists active-list-id :products product-id :checked] not))))

(re/reg-event-db ::toggle-pinned
  (fn [db [_ list-id]]
    (let [active-list-id (:active-list db)]
      (update-in db [:lists active-list-id :pinned] not))))

(re/reg-event-fx ::add-product-to-list
  (fn [{db :db} [_ product-id]]
    (let [active-list-id (:active-list db)
          item-already-in-list (get-in db [:lists active-list-id :products product-id])
          new-product-map {:product product-id :amount 1 :checked false}
          new-db (assoc-in db [:lists active-list-id :products product-id] new-product-map)]
      (if item-already-in-list
        {::item-already-in-list nil}
        {:db new-db
         :dispatch [::update-search nil]}))))

(re/reg-event-db ::update-new-product
  (fn [db [_ new-product]]
    (assoc db :new-product new-product)))

(re/reg-event-db ::update-new-product-name
  (fn [db [_ new-name]]
    (assoc-in db [:new-product :name] new-name)))

(re/reg-event-db ::update-new-product-category
  (fn [db [_ new-category]]
    (assoc-in db [:new-product :category] new-category)))

(re/reg-event-fx ::save-new-product
  (fn [{db :db}]
    (let [new-product (:new-product db)
          new-db (assoc-in db [:products (:id new-product)] new-product)]
      {:db new-db
       :dispatch [::update-new-product nil]})))

(re/reg-event-fx ::init-new-product
  (fn [{db :db}]
    (let [new-product (helpers/new-product-placeholder)]
      {:dispatch [::update-new-product new-product]})))

(re/reg-event-fx ::init-new-list
  (fn [{db :db}]
    (let [new-list (helpers/new-list-placeholder)
          new-list-id (:id new-list)
          new-db (assoc-in db [:lists new-list-id] new-list)]
      {:db new-db
       :dispatch [::set-active-list new-list-id]})))

(re/reg-event-db
 ::initialize-db
 (fn [_ _]
   default-db))
