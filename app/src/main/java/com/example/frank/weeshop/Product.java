package com.example.frank.weeshop;

    public class Product {
        private String product_id;
        private String name;
        private double price;
        private String quantity;

        public Product(String product_id, double price, String quantity) {
            this.product_id = product_id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getProduct_id() {
            return product_id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getQuantity() {
            return quantity;
        }
    }
