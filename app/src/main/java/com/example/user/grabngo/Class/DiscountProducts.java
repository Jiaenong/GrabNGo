package com.example.user.grabngo.Class;

public class DiscountProducts {
    private String name, ref, promoRef;

    public DiscountProducts() {
    }

    public DiscountProducts(String name, String ref, String promoRef) {
        this.name = name;
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPromoRef() {
        return promoRef;
    }

    public void setPromoRef(String promoRef) {
        this.promoRef = promoRef;
    }
}
