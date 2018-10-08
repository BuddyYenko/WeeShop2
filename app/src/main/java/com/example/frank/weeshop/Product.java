package com.example.frank.weeshop;

import java.io.Serializable;

public class Product implements Serializable{
        public String product_id;
        public String name;
        public double price;
        public String quantity;
        public double tv_total;
        public double grandTotal;


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

        public double getTotal() {
            return tv_total;
        }

        public double getGrandTotal() {
                return grandTotal;
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
        public void setTotal(double tv_total) {
            tv_total = tv_total;
        }
        public void setGrandTotal(double grandTotal) {
        grandTotal = grandTotal;
    }



    }
