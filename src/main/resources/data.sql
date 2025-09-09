INSERT INTO products (name, price, image_url)
VALUES
    ('치킨', 25000, 'https://i.namu.wiki/i/edfWrspIXA95Dr3cg-07pth8ygVfRm6c_QDSt3LCLrNyIpf2-uKX8Q5tFbLvyfKGkXcdYk9tqqxaUaHsm5FHtd2A4Njn_ZoBjD5zZYQYXNv5Bc4I7wgWga6YH-AN_zPahEZvzJMDETMX7g_Xf0Ahfw.webp'),
    ('피자', 20000, 'https://www.youngmanpizza.co.kr/upload/M_ori/6678cf42c5da9.jpg'),
    ('햄버거', 8900, 'https://i.namu.wiki/i/4-iX1WOxRnJOYQXM1IzJYwAeHtDRGv4HnO6xR0s6ZpsiltAmpO_RC7oyPXy9vIOYrFjapiqUhgZFH0O96h8g4w.webp');

INSERT INTO options (name, quantity, product_id) VALUES ('후라이드 치킨', 999, 1);
INSERT INTO options (name, quantity, product_id) VALUES ('양념 치킨', 999, 1);

INSERT INTO options (name, quantity, product_id) VALUES ('페퍼로니 피자', 999, 2);
INSERT INTO options (name, quantity, product_id) VALUES ('불고기 피자', 999, 2);

INSERT INTO options (name, quantity, product_id) VALUES ('햄버거 단품', 999, 3);
INSERT INTO options (name, quantity, product_id) VALUES ('햄버거 세트', 999, 3);
