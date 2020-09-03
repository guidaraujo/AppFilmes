package com.example.listadefilmesapp.model;

import java.util.List;

public class Resultado {
    public List<Items> items;

    public class Items {
        public Pagemap pagemap;

        public class Pagemap {
            public List<CseImage> cse_image;

            public class CseImage {
                public String src;

            }
        }
    }
}
