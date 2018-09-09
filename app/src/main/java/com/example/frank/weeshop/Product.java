package com.example.frank.weeshop;

    public class Product {
        private String product_id;
        private String name;
        private double price;
        private String quantity;

        public Product(String product_id, String name, double price, String quantity) {
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



        public void setProduct_id(String product_id) {
            product_id = product_id;
        }

        public void setName(String name) {
            name = name;
        }
        public void setPrice(double price) {
            price = price;
        }
        public void setQuantity(String quantity) {
            quantity = quantity;
        }



        public String getJsonObject(){
            return "{Product_id:"+product_id+",name:"+name+",price:"+price+",quantity:"+quantity+"}";
        }
    }
