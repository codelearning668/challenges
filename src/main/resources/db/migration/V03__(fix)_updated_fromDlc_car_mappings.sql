UPDATE car
SET from_dlc = false
WHERE simulator_id = 1
  AND (brand, name) IN (
    ('Abarth', '500 Assetto Corse'),
    ('Abarth', '500 EsseEsse'),
    ('Abarth', '500 EsseEsse Step 1'),
    ('Abarth', '595 SS'),
    ('Abarth', '595 SS Step 1'),
    ('Abarth', '595 SS Step 2'),

    ('Alfa Romeo', '33 Stradale'),
    ('Alfa Romeo', 'Giulia Quadrifoglio'),
    ('Alfa Romeo', 'Giulietta QV'),
    ('Alfa Romeo', 'Qiulietta QV Launch Edition 2014'),
    ('Alfa Romeo', 'Mito QV'),

    ('Audi', 'R8 LMS Ultra'),
    ('Audi', 'Sport quattro'),
    ('Audi', 'Sport quattro S1 E2'),
    ('Audi', 'Sport quattro Step 1'),

    ('BMW', '1M'),
    ('BMW', '1M Stage 3'),
    ('BMW', 'M3 E30'),
    ('BMW', 'M3 E30 Drift'),
    ('BMW', 'M3 E30 Gr.A 92'),
    ('BMW', 'M3 E30 Group A'),
    ('BMW', 'M3 E30 Step 1'),
    ('BMW', 'M3 E92'),
    ('BMW', 'M3 E92 Step 1'),
    ('BMW', 'M3 E92 Drift'),
    ('BMW', 'M3 GT2'),
    ('BMW', 'M4 Akrapovic'),
    ('BMW', 'Z4 E89'),
    ('BMW', 'Z4 E89 Drift'),
    ('BMW', 'Z4 E89 Step 1'),
    ('BMW', 'Z4 GT3'),

    ('Chevrolet', 'Corvette C7R'),

    ('Ferrari', '312T'),
    ('Ferrari', '458 GT2'),
    ('Ferrari', '458 Italia'),
    ('Ferrari', '458 Italia Stage 3'),
    ('Ferrari', '599XX EVO'),
    ('Ferrari', 'F40'),
    ('Ferrari', 'F40 Stage 3'),
    ('Ferrari', 'FXX K'),
    ('Ferrari', 'LaFerrari'),

    ('Ford', 'Escort RS1600'),
    ('Ford', 'GT40'),

    ('KTM', 'X-Bow R'),

    ('Lamborghini', 'Countach'),
    ('Lamborghini', 'Countach S1'),
    ('Lamborghini', 'Gallardo SL Step 3'),
    ('Lamborghini', 'Huracan GT3'),
    ('Lamborghini', 'Huracan Performante'),
    ('Lamborghini', 'Huracan ST'),
    ('Lamborghini', 'Miura P400 SV'),
    ('Lamborghini', 'Sesto Elemento'),

    ('Lotus', '2-Eleven'),
    ('Lotus', '2-Eleven GT4'),
    ('Lotus', 'Elise SC'),
    ('Lotus', 'Elise SC Step 1'),
    ('Lotus', 'Elise SC Step 2'),
    ('Lotus', 'Evora GTC'),
    ('Lotus', 'Evora GTE'),
    ('Lotus', 'Evora GTE Carbon'),
    ('Lotus', 'Evora GX'),
    ('Lotus', 'Evora S'),
    ('Lotus', 'Evora S Stage 2'),
    ('Lotus', 'Exige 240R'),
    ('Lotus', 'Exige 240R Stage 3'),
    ('Lotus', 'Exige S'),
    ('Lotus', 'Exige S roadster'),
    ('Lotus', 'Exige Scura'),
    ('Lotus', 'Exige V6 Cup'),
    ('Lotus', 'Exos 125'),
    ('Lotus', 'Exos 125 Stage 1'),
    ('Lotus', '98T'),
    ('Lotus', 'Type 49'),

    ('Maserati', 'Alfieri'),
    ('Maserati', 'Levante S'),
    ('Maserati', 'Quattroporte GTS'),

    ('Mazda', '787B'),
    ('Mazda', 'Miata NA'),

    ('McLaren', '650S GT3'),
    ('McLaren', 'F1 GTR'),
    ('McLaren', 'MP4-12C'),
    ('McLaren', 'MP4-12C GT3'),
    ('McLaren', 'P1'),

    ('Mercedes', 'SLS AMG'),
    ('Mercedes', 'SLS AMG GT3'),
    ('Mercedes', '190E EVO II'),
    ('Mercedes', 'AMG GT3'),
    ('Mercedes', 'C9 1989 LM'),

    ('Nissan', 'GT-R GT3'),

    ('Pagani', 'Huayra'),
    ('Pagani', 'Huayra BC'),
    ('Pagani', 'Zonda R'),

    ('Porsche', 'Cayenne Turbo S'),
    ('Porsche', 'Macan Turbo'),
    ('Porsche', 'Panamera Turbo'),

    ('RUF', 'CTR Yellowbird'),
    ('RUF', 'RT12 R'),
    ('RUF', 'RT12 R AWD'),

    ('Scuderia Glickenhaus', 'P4/5 Competizione 2011'),
    ('Scuderia Glickenhaus', 'SCG 003C'),

    ('Shelby', 'Cobra 427 S/C'),

    ('Tatuus', 'FA01')
);

