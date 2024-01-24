    package com.example.acadbudv2;

    public class rating_item {
            private String studentName;

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        private float rating;

            public rating_item(String studentName, float rating) {
                this.studentName = studentName;
                this.rating = rating;
            }

            public String getStudentName() {
                return studentName;
            }

            public float getRating() {
                return rating;
            }
        }