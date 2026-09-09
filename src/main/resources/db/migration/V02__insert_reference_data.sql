
-- activate unaccent function
CREATE EXTENSION IF NOT EXISTS unaccent;

INSERT INTO simulator (
    version,
    name,
    created_at
) VALUES
    (1, 'ASSETO_CORSA', now()),
    (1, 'ASSETO_CORSA_COMPETIZIONE', now()),
    (1, 'WRC_GENERATIONS', now());

-- all assetto corsa ultimate edition tracks (will be potentially differently structured when the simulator dependency is implemented)
INSERT INTO track (
    country,
    name,
    length_km,
    version,
    country_search,
    name_search,
    from_dlc,
    simulator_id,
    created_at
)
SELECT
    country,
    name,
    length_km,
    version,
    lower(unaccent(country)),
    lower(unaccent(name)),
    from_dlc,
    simulator_id,
    now()
FROM (
    VALUES
        ('Spain', 'Barcelona - GP', 4.655, 1, true, 1),
        ('Spain', 'Barcelona - Moto', 4.727, 1, true, 1),

        ('USA', 'Black Cat County', 6.478, 1, false, 1),
        ('USA', 'Black Cat County - Long', 11.244, 1, false, 1),
        ('USA', 'Black Cat County - Short', 6.542, 1, false, 1),

        ('Great Britain', 'Brands Hatch - GP', 3.908, 1, true, 1),
        ('Great Britain', 'Brands Hatch - Indy', 1.929, 1, true, 1),

        ('Scotland', 'Highlands', 8.152, 1, false, 1),
        ('Scotland', 'Highlands Drift', 5.167, 1, false, 1),
        ('Scotland', 'Highlands Long', 12.191, 1, false, 1),
        ('Scotland', 'Highlands Short', 1.714, 1, false, 1),

        ('Italy', 'Imola', 4.909, 1, false, 1),
        ('USA', 'Laguna Seca', 3.602, 1, false, 1),
        ('Italy', 'Magione', 2.507, 1, false, 1),

        ('Italy', 'Monza', 5.793, 1, false, 1),
        ('Italy', 'Monza 1966 - Full Course', 10.000, 1, false, 1),
        ('Italy', 'Monza 1966 - Junior Course', 2.405, 1, false, 1),
        ('Italy', 'Monza 1966 - Road Course', 5.793, 1, false, 1),

        ('Italy', 'Mugello', 5.245, 1, false, 1),

        ('Germany', 'Nordschleife', 20.832, 1, true, 1),
        ('Germany', 'Nordschleife - Endurance', 25.378, 1, true, 1),
        ('Germany', 'Nordschleife - Endurance Cup', 24.433, 1, true, 1),

        ('Germany', 'Nurburgring - GP', 5.148, 1, false, 1),
        ('Germany', 'Nurburgring - GP (GT)', 5.137, 1, false, 1),
        ('Germany', 'Nurburgring - Sprint', 3.629, 1, false, 1),
        ('Germany', 'Nurburgring - Sprint (GT)', 3.618, 1, false, 1),

        ('Austria', 'Red Bull Ring GP', 4.326, 1, true, 1),
        ('Austria', 'Red Bull Ring National', 2.336, 1, true, 1),

        ('Great Britain', 'Silverstone - International', 3.619, 1, false, 1),
        ('Great Britain', 'Silverstone - National', 2.638, 1, false, 1),
        ('Great Britain', 'Silverstone 1967', 4.710, 1, false, 1),
        ('Great Britain', 'Silverstone GP', 5.901, 1, false, 1),

        ('Belgium', 'Spa', 7.004, 1, false, 1),

        ('Italy', 'Vallelunga', 4.085, 1, false, 1),
        ('Italy', 'Vallelunga - Classic', 3.222, 1, false, 1),
        ('Italy', 'Vallelunga - Club', 1.746, 1, false, 1),

        ('Netherlands', 'Zandvoort', 4.307, 1, false, 1),

           -- all WRC Generations tracks (comments behind values are elevations in meters -> future update of Track entity)

        	('Monte Carlo', 'Monte Carlo Shakedown', 1.97, 1, false, 3), -- 97
            ('Monte Carlo', 'Agniéres-en-Devoluy', 7.47, 1, false, 3), -- 404
            ('Monte Carlo', 'Agniéres-en-Devoluy reverse', 7.44, 1, false, 3), -- 398
            ('Monte Carlo', 'Luceram', 5.94, 1, false, 3), -- 328
            ('Monte Carlo', 'Luceram reverse', 6.03, 1, false, 3), -- 332
            ('Monte Carlo', 'Col De Braus', 7.20, 1, false, 3), -- 485
            ('Monte Carlo', 'Col De Braus reverse', 7.24, 1, false, 3), -- 488
            ('Monte Carlo', 'La Bolléne-Vésuble', 20.97, 1, false, 3), -- 1189
            ('Monte Carlo', 'La Bolléne-Vésuble reverse', 20.97, 1, false, 3), -- 1173

            ('Sweden', 'Sweden Shakedown (PTSD)', 3.18, 1, false, 3), -- 128
            ('Sweden', 'Sävar', 7.89, 1, false, 3), -- 248
            ('Sweden', 'Sävar reverse', 7.89, 1, false, 3), -- 247
            ('Sweden', 'Brattby', 7.36, 1, false, 3), -- 341
            ('Sweden', 'Brattby reverse', 7.36, 1, false, 3), -- 342
            ('Sweden', 'Kroksjö', 8.30, 1, false, 3), -- 360
            ('Sweden', 'Kroksjö reverse', 8.30, 1, false, 3), -- 363
            ('Sweden', 'Örträsk', 25.90, 1, false, 3), -- 1044
            ('Sweden', 'Örträsk reverse', 25.90, 1, false, 3), -- 1045

            ('Croatia', 'Croatia Shakedown', 3.22, 1, false, 3), -- 162
            ('Croatia', 'Grdanjci', 6.02, 1, false, 3), -- 315
            ('Croatia', 'Grdanjci reverse', 6.10, 1, false, 3), -- 312
            ('Croatia', 'Jaškovo', 5.53, 1, false, 3), -- 269
            ('Croatia', 'Jaškovo reverse', 5.54, 1, false, 3), -- 270
            ('Croatia', 'Rude', 7.09, 1, false, 3), -- 318
            ('Croatia', 'Rude reverse', 7.09, 1, false, 3), -- 318
            ('Croatia', 'Kostanjevac', 19.88, 1, false, 3), -- 941
            ('Croatia', 'Kostanjevac reverse', 19.96, 1, false, 3), -- 939

            ('Portugal', 'Portugal Shakedown', 2.73, 1, false, 3), -- 183
            ('Portugal', 'Felgveiras', 8.46, 1, false, 3), -- 443
            ('Portugal', 'Felgveiras reverse', 8.46, 1, false, 3), -- 440
            ('Portugal', 'Arganil', 9.66, 1, false, 3), -- 583
            ('Portugal', 'Arganil reverse', 9.67, 1, false, 3), -- 586
            ('Portugal', 'Cabeceiras de Basto', 20.07, 1, false, 3), -- 1111
            ('Portugal', 'Cabeceiras de Basto reverse', 20.07, 1, false, 3), -- 1113
            ('Portugal', 'Lousada', 3.58, 1, false, 3), -- 47

            ('Italy-Sardinia', 'Sardinia Shakedown', 1.81, 1, false, 3), -- 162
            ('Italy-Sardinia', 'Baranta', 5.17, 1, false, 3), -- 330
            ('Italy-Sardinia', 'Baranta reverse', 5.26, 1, false, 3), -- 340
            ('Italy-Sardinia', 'Lerno', 4.13, 1, false, 3), -- 365
            ('Italy-Sardinia', 'Lerno reverse', 4.15, 1, false, 3), -- 366
            ('Italy-Sardinia', 'Monti di Ala', 13.21, 1, false, 3), -- 956
            ('Italy-Sardinia', 'Monti di Ala reverse', 13.22, 1, false, 3), -- 960
            ('Italy-Sardinia', 'Ittiri Arena', 2.08, 1, false, 3), -- 41

            ('Kenya', 'Kenya Shakedown', 3.36, 1, false, 3), -- 61
            ('Kenya', 'Ngema', 7.71, 1, false, 3), -- 138
            ('Kenya', 'Ngema reverse', 7.71, 1, false, 3), -- 138
            ('Kenya', 'Seyabei', 6.25, 1, false, 3), -- 153
            ('Kenya', 'Seyabei reverse', 6.25, 1, false, 3), -- 153
            ('Kenya', 'Nitulele', 14.32, 1, false, 3), -- 296
            ('Kenya', 'Nitulele reverse', 14.31, 1, false, 3), -- 292
            ('Kenya', 'Kasarani', 4.45, 1, false, 3), -- 36

            ('Estonia', 'Estonia Shakedown', 1.57, 1, false, 3), -- 53
            ('Estonia', 'Elva', 8.35, 1, false, 3), -- 506
            ('Estonia', 'Elva reverse', 8.36, 1, false, 3), -- 509
            ('Estonia', 'Otepää', 5.70, 1, false, 3), -- 260
            ('Estonia', 'Otepää reverse', 5.70, 1, false, 3), -- 261
            ('Estonia', 'Kanepi', 14.04, 1, false, 3), -- 755
            ('Estonia', 'Kanepi reverse', 14.04, 1, false, 3), -- 757
            ('Estonia', 'Tartu', 0.97, 1, false, 3), -- 31

            ('Finland', 'Finland Shakedown', null, 1, false, 3), -- 64
            ('Finland', 'Arvaja', 9.65, 1, false, 3), -- 386
            ('Finland', 'Arvaja reverse', 9.65, 1, false, 3), -- 379
            ('Finland', 'Pihlajakoski', 9.79, 1, false, 3), -- 579
            ('Finland', 'Pihlajakoski reverse', 9.79, 1, false, 3), -- 578
            ('Finland', 'Laukaa', 21.10, 1, false, 3), -- 1048
            ('Finland', 'Laukaa reverse', 21.09, 1, false, 3), -- 1044
            ('Finland', 'Harju', 2.27, 1, false, 3), -- 82

            ('Belgium', 'Belgium Shakedown', 1.63, 1, false, 3), -- 99
            ('Belgium', 'Dikkebus', 5.26, 1, false, 3), -- 155
            ('Belgium', 'Dikkebus reverse', 5.27, 1, false, 3), -- 154
            ('Belgium', 'Kemmelberg', 6.78, 1, false, 3), -- 320
            ('Belgium', 'Kemmelberg reverse', 6.78, 1, false, 3), -- 320
            ('Belgium', 'ZonneBeke', 12.23, 1, false, 3), -- 477
            ('Belgium', 'ZonneBeke reverse', 12.24, 1, false, 3), -- 477

            ('Greece', 'Acropolis Shakedown', 2.65, 1, false, 3), -- 109
            ('Greece', 'Psatha', 6.93, 1, false, 3), -- 404
            ('Greece', 'Psatha reverse', 6.94, 1, false, 3), -- 405
            ('Greece', 'Amfissa', 6.91, 1, false, 3), -- 562
            ('Greece', 'Amfissa reverse', 6.91, 1, false, 3), -- 562
            ('Greece', 'Paleohori', 4.87, 1, false, 3), -- 584
            ('Greece', 'Paleohori reverse', 4.87, 1, false, 3), -- 577
            ('Greece', 'Mendenitsa', 19.01, 1, false, 3), -- 1565
            ('Greece', 'Mendenitsa reverse', 19.02, 1, false, 3), -- 1561

            ('New Zeland', 'New Zeland Shakedown', 2.84, 1, false, 3), -- 129
            ('New Zeland', 'Te Hutewai', 8.35, 1, false, 3), -- 353
            ('New Zeland', 'Te Hutewai reverse', 8.35, 1, false, 3), -- 356
            ('New Zeland', 'Batley', 6.83, 1, false, 3), -- 332
            ('New Zeland', 'Batley reverse', 6.83, 1, false, 3), -- 328
            ('New Zeland', 'Brooks', 6.92, 1, false, 3), -- 285
            ('New Zeland', 'Brooks reverse', 6.93, 1, false, 3), -- 287
            ('New Zeland', 'Te Akau South', 22.00, 1, false, 3), -- 998
            ('New Zeland', 'Te Akau South reverse', 21.99, 1, false, 3), -- 1007

            ('Spain', 'Spain Shakedown', 2.67, 1, false, 3), -- 174
            ('Spain', 'Riudecanyes', 7.04, 1, false, 3), -- 357
            ('Spain', 'Riudecanyes reverse', 7.04, 1, false, 3), -- 361
            ('Spain', 'Savallá', 5.74, 1, false, 3), -- 181
            ('Spain', 'Savallá reverse', 5.74, 1, false, 3), -- 181
            ('Spain', 'Querol', 14.39, 1, false, 3), -- 679
            ('Spain', 'Querol reverse', 14.38, 1, false, 3), -- 681
            ('Spain', 'Barcelona', 3.38, 1, false, 3), -- 68

            ('Japan', 'Japan Shakedown', 2.87, 1, false, 3), -- 94
            ('Japan', 'Okazaki', 8.08, 1, false, 3), -- 347
            ('Japan', 'Okazaki reverse', 7.91, 1, false, 3), -- 329
            ('Japan', 'Nagakute', 7.14, 1, false, 3), -- 391
            ('Japan', 'Nagakute reverse', 7.16, 1, false, 3), -- 391
            ('Japan', 'Shinshiro', 7.32, 1, false, 3), -- 324
            ('Japan', 'Shinshiro reverse', 7.32, 1, false, 3), -- 323
            ('Japan', 'Shitara', 22.60, 1, false, 3), -- 1052
            ('Japan', 'Shitara reverse', 22.59, 1, false, 3), -- 1054

            ('Argentina', 'Argentina Shakedown', 2.23, 1, false, 3), -- 149
            ('Argentina', 'El Condor', 7.14, 1, false, 3), -- 406
            ('Argentina', 'El Condor reverse', 7.11, 1, false, 3), -- 404
            ('Argentina', 'Cuchilla Nevada', 6.88, 1, false, 3), -- 398
            ('Argentina', 'Cuchilla Nevada reverse', 6.69, 1, false, 3), -- 382
            ('Argentina', 'Cantera', 14.74, 1, false, 3), -- 853
            ('Argentina', 'Cantera reverse', 14.71, 1, false, 3), -- 851
            ('Argentina', 'Parque Tematico', 6.60, 1, false, 3), -- 127

            ('Chile', 'Chile Shakedown', 2.82, 1, false, 3), -- 228
            ('Chile', 'Licay', 6.38, 1, false, 3), -- 458
            ('Chile', 'Licay reverse', 6.41, 1, false, 3), -- 465
            ('Chile', 'Biobio', 6.28, 1, false, 3), -- 351
            ('Chile', 'Biobio reverse', 6.29, 1, false, 3), -- 350
            ('Chile', 'Pelún', 8.11, 1, false, 3), -- 467
            ('Chile', 'Pelún reverse', 8.03, 1, false, 3), -- 460
            ('Chile', 'El Puma', 21.03, 1, false, 3), -- 1286
            ('Chile', 'El Puma reverse', 21.02, 1, false, 3), -- 1280

            ('Germany', 'Germany Shakedown', 3.01, 1, false, 3), -- 136
            ('Germany', 'Moselland', 7.86, 1, false, 3), -- 413
            ('Germany', 'Moselland reverse', 7.87, 1, false, 3), -- 411
            ('Germany', 'Freisen', 6.87, 1, false, 3), -- 315
            ('Germany', 'Freisen reverse', 6.85, 1, false, 3), -- 314
            ('Germany', 'Mittelmosel', 15.61, 1, false, 3), -- 765
            ('Germany', 'Mittelmosel reverse', 15.63, 1, false, 3), -- 764
            ('Germany', 'Arena Panzerplatte', 2.88, 1, false, 3), -- 124

            ('Mexico', 'Mexico Shakedown', 2.22, 1, false, 3), -- 213
            ('Mexico', 'Media Luna', 3.98, 1, false, 3), -- 213
            ('Mexico', 'Media Luna reverse', 3.93, 1, false, 3), -- 212
            ('Mexico', 'Ibarrilla', 8.26, 1, false, 3), -- 485
            ('Mexico', 'Ibarrilla reverse', 8.25, 1, false, 3), -- 484
            ('Mexico', 'El Chocolate', 18.30, 1, false, 3), -- 1197
            ('Mexico', 'El Chocolate reverse', 18.32, 1, false, 3), -- 1199
            ('Mexico', 'Autódromo de León', 2.58, 1, false, 3), -- 12

            ('Italy-Sanremo', 'Sanremo Shakedown', 2.01, 1, false, 3), -- 74
            ('Italy-Sanremo', 'Ronde', 7.24, 1, false, 3), -- 497
            ('Italy-Sanremo', 'Ronde reverse', 7.24, 1, false, 3), -- 494

            ('France-Corsica', 'Corse Shakedown', 2.82, 1, false, 3), -- 67
            ('France-Corsica', 'Pietrosella', 7.54, 1, false, 3), -- 287
            ('France-Corsica', 'Pietrosella reverse', 7.50, 1, false, 3), -- 286

            ('Turkey', 'Turkey Shakedown', 2.29, 1, false, 3), -- 123
            ('Turkey', 'Datca', 6.62, 1, false, 3), -- 383
            ('Turkey', 'Datca reverse', 6.53, 1, false, 3), -- 380
            ('Turkey', 'Cicekli', 6.53, 1, false, 3), -- 383
            ('Turkey', 'Cicekli reverse', 6.53, 1, false, 3), -- 376
            ('Turkey', 'Yesilbelde', 14.55, 1, false, 3), -- 855
            ('Turkey', 'Yesilbelde reverse', 14.52, 1, false, 3), -- 848
            ('Turkey', 'Marmaris', 2.20, 1, false, 3), -- 1

            ('Wales', 'Wales Shakedown', 1.75, 1, false, 3), -- 114
            ('Wales', 'Hafren', 6.01, 1, false, 3), -- 379
            ('Wales', 'Hafren reverse', 6.06, 1, false, 3), -- 399
            ('Wales', 'Great Orme', 4.58, 1, false, 3), -- 160
            ('Wales', 'Great Orme reverse', 4.52, 1, false, 3), -- 161
            ('Wales', 'Brenig', 6.40, 1, false, 3), -- 360
            ('Wales', 'Brenig reverse', 6.40, 1, false, 3), -- 368
            ('Wales', 'Dyfi', 17.49, 1, false, 3), -- 1022
            ('Wales', 'Dyfi', 17.43, 1, false, 3) -- 1019

         )AS data(country, name, length_km, version, from_dlc, simulator_id);


-- all assetto corsa ultimate edition cars
INSERT INTO car (
    brand,
    name,
    horse_power,
    torque,
    wheel_drive,
    brand_search,
    name_search,
    version,
    from_dlc,
    simulator_id,
    created_at
)
select
    brand,
    name,
    horse_power,
    torque,
    wheel_drive,
    lower(unaccent(brand)),
    lower(unaccent(name)),
    version,
    from_dlc,
    simulator_id,
    now()
FROM (
    VALUES
        ('Abarth', '500 Assetto Corse', 195, 302, 'FRONT', 1, true, 1),
        ('Abarth', '500 EsseEsse', 160, 230, 'FRONT', 1, true, 1),
        ('Abarth', '500 EsseEsse Step 1', 175, 245, 'FRONT', 1, true, 1),
        ('Abarth', '595 SS', 32, 44, 'REAR', 1, true, 1),
        ('Abarth', '595 SS Step 1', 32, 44, 'REAR', 1, true, 1),
        ('Abarth', '595 SS Step 2', 65, 80, 'REAR', 1, true, 1),

        ('Alfa Romeo', '155 TI V6', 420, 294, 'ALL', 1, true, 1),
        ('Alfa Romeo', '33 Stradale', 230, 206, 'REAR', 1, true, 1),
        ('Alfa Romeo', '4C', 240, 350, 'REAR', 1, true, 1),
        ('Alfa Romeo', 'GTA', 170, 200, 'REAR', 1, true, 1),
        ('Alfa Romeo', 'Giulia Quadrifoglio', 510, 600, 'REAR', 1, true, 1),
        ('Alfa Romeo', 'Mito QV', 168, 250, 'FRONT', 1, true, 1),
        ('Alfa Romeo', 'Giulietta QV', 235, 349, 'FRONT', 1, true, 1),
        ('Alfa Romeo', 'Qiulietta QV Launch Edition 2014', 240, 349, 'FRONT', 1, true, 1),

        ('Audi', 'R18 e-tron quattro 2014', 700, 850, 'ALL', 1, true, 1),
        ('Audi', 'R8 LMS 2016', 500, 500, 'REAR', 1, true, 1),
        ('Audi', 'R8 LMS Ultra', 570, 500, 'REAR', 1, true, 1),
        ('Audi', 'R8 V10 Plus', 550, 540, 'ALL', 1, true, 1),
        ('Audi', 'S1', 231, 370, 'ALL', 1, true, 1),
        ('Audi', 'Sport quattro', 306, 350, 'ALL', 1, true, 1),
        ('Audi', 'Sport quattro S1 E2', 540, 570, 'ALL', 1, true, 1),
        ('Audi', 'Sport quattro Step 1', 373, 425, 'ALL', 1, true, 1),
        ('Audi', 'TT Cup', 310, 400, 'FRONT', 1, true, 1),
        ('Audi', 'TT RS (VLN)', 390, 530, 'FRONT', 1, true, 1),

        ('BMW', '1M', 340, 500, 'REAR', 1, true, 1),
        ('BMW', '1M Stage 3', 400, 580, 'REAR', 1, true, 1),
        ('BMW', 'M235i Racing', 333, 500, 'REAR', 1, true, 1),
        ('BMW', 'M3 E30', 238, 240, 'REAR', 1, true, 1),
        ('BMW', 'M3 E30 Drift', 343, 365, 'REAR', 1, true, 1),
        ('BMW', 'M3 E30 Gr.A 92', 350, 310, 'REAR', 1, true, 1),
        ('BMW', 'M3 E30 Group A', 290, 310, 'REAR', 1, true, 1),
        ('BMW', 'M3 E30 Step 1', 238, 240, 'REAR', 1, true, 1),
        ('BMW', 'M3 E92', 414, 400, 'REAR', 1, true, 1),
        ('BMW', 'M3 E92 Step 1', 414, 400, 'REAR', 1, true, 1),
        ('BMW', 'M3 E92 Drift', 414, 400, 'REAR', 1, true, 1),
        ('BMW', 'M3 GT2', 485, 499, 'REAR', 1, true, 1),
        ('BMW', 'M4', 431, 550, 'REAR', 1, true, 1),
        ('BMW', 'M4 Akrapovic', 445, 589, 'REAR', 1, true, 1),
        ('BMW', 'Z4 E89', 330, 500, 'REAR', 1, true, 1),
        ('BMW', 'Z4 E89 Drift', 390, 580, 'REAR', 1, true, 1),
        ('BMW', 'Z4 E89 Step 1', 330, 500, 'REAR', 1, true, 1),
        ('BMW', 'Z4 GT3', 530, 520, 'REAR', 1, true, 1),

        ('Chevrolet', 'Corvette C7 Stingray', 455, 625, 'REAR', 1, true, 1),
        ('Chevrolet', 'Corvette C7R', 495, 650, 'REAR', 1, true, 1),

        ('Ferrari', '250 GTO', 300, 294, 'REAR', 1, true, 1),
        ('Ferrari', '312/67', 390, null, 'REAR', 1, true, 1),
        ('Ferrari', '312T', 495, 308, 'REAR', 1, true, 1),
        ('Ferrari', '330 P4', 450, null, 'REAR', 1, true, 1),
        ('Ferrari', '458 GT2', 470, 520, 'REAR', 1, true, 1),
        ('Ferrari', '458 Italia', 570, 540, 'REAR', 1, true, 1),
        ('Ferrari', '458 Italia Stage 3', 570, 540, 'REAR', 1, true, 1),
        ('Ferrari', '488 GT3', 500, 640, 'REAR', 1, true, 1),
        ('Ferrari', '488 GTB', 660, 760, 'REAR', 1, true, 1),
        ('Ferrari', '599XX EVO', 750, 700, 'REAR', 1, true, 1),
        ('Ferrari', '812 Superfast', 800, 718, 'REAR', 1, true, 1),
        ('Ferrari', 'F138', 763, 310, 'REAR', 1, true, 1),
        ('Ferrari', 'F2004', 865, null, 'REAR', 1, true, 1),
        ('Ferrari', 'F40', 478, 577, 'REAR', 1, true, 1),
        ('Ferrari', 'F40 Stage 3', 610, 715, 'REAR', 1, true, 1),
        ('Ferrari', 'FXX K', 1050, 900, 'REAR', 1, true, 1),
        ('Ferrari', 'GTO', 400, 496, 'REAR', 1, true, 1),
        ('Ferrari', 'LaFerrari', 963, 900, 'REAR', 1, true, 1),
        ('Ferrari', 'SF15-T', 840, 721, 'REAR', 1, true, 1),
        ('Ferrari', 'SF70H', null, null, 'REAR', 1, true, 1),

        ('Ford', 'Escort RS1600', 260, 200, 'REAR', 1, true, 1),
        ('Ford', 'GT40', 430, 530, 'REAR', 1, true, 1),
        ('Ford', 'Mustang 2015', 435, 542, 'REAR', 1, true, 1),

        ('KTM', 'X-Bow R', 300, 400, 'REAR', 1, true, 1),

        ('Lamborghini', 'Aventador SV', 750, 690, 'ALL', 1, true, 1),
        ('Lamborghini', 'Countach', 422, 500, 'REAR', 1, true, 1),
        ('Lamborghini', 'Countach S1', 442, 500, 'REAR', 1, true, 1),
        ('Lamborghini', 'Gallardo SL', 570, 540, 'ALL', 1, true, 1),
        ('Lamborghini', 'Gallardo SL Step 3', 1200, null, 'ALL', 1, true, 1),
        ('Lamborghini', 'Huracan GT3', 600, 500, 'REAR', 1, true, 1),
        ('Lamborghini', 'Huracan Performante', 640, 600, 'ALL', 1, true, 1),
        ('Lamborghini', 'Huracan ST', 620, 570, 'REAR', 1, true, 1),
        ('Lamborghini', 'Miura P400 SV', 385, 400, 'REAR', 1, true, 1),
        ('Lamborghini', 'Sesto Elemento', 570, 540, 'ALL', 1, true, 1),

        ('Lotus', '2-Eleven', 252, 242, 'REAR', 1, true, 1),
        ('Lotus', '2-Eleven GT4', 270, 252, 'REAR', 1, true, 1),
        ('Lotus', '3-Eleven', 460, 525, 'REAR', 1, true, 1),
        ('Lotus', 'Elise SC', 217, 242, 'REAR', 1, true, 1),
        ('Lotus', 'Elise SC Step 1', 217, 242, 'REAR', 1, true, 1),
        ('Lotus', 'Elise SC Step 2', 217, 242, 'REAR', 1, true, 1),
        ('Lotus', 'Evora GTC', 450, 460, 'REAR', 1, true, 1),
        ('Lotus', 'Evora GTE', 420, 461, 'REAR', 1, true, 1),
        ('Lotus', 'Evora GTE Carbon', 420, 461, 'REAR', 1, true, 1),
        ('Lotus', 'Evora GX', 440, 460, 'REAR', 1, true, 1),
        ('Lotus', 'Evora S', 345, 400, 'REAR', 1, true, 1),
        ('Lotus', 'Evora S Stage 2', 400, 420, 'REAR', 1, true, 1),
        ('Lotus', 'Exige 240R', 243, 236, 'REAR', 1, true, 1),
        ('Lotus', 'Exige 240R Stage 3', 270, 270, 'REAR', 1, true, 1),
        ('Lotus', 'Exige S', 345, 400, 'REAR', 1, true, 1),
        ('Lotus', 'Exige S roadster', 345, 400, 'REAR', 1, true, 1),
        ('Lotus', 'Exige Scura', 257, 243, 'REAR', 1, true, 1),
        ('Lotus', 'Exige V6 Cup', 345, 400, 'REAR', 1, true, 1),
        ('Lotus', 'Exos 125', 640, 4540, 'REAR', 1, true, 1),
        ('Lotus', 'Exos 125 Stage 1', 750, null, 'REAR', 1, true, 1),
        ('Lotus', '720', 440, 332, 'REAR', 1, true, 1),
        ('Lotus', '98T', 1000, 800, 'REAR', 1, true, 1),
        ('Lotus', 'Type 25', 195, 160, 'REAR', 1, true, 1),
        ('Lotus', 'Type 49', 410, 350, 'REAR', 1, true, 1),

        ('Maserati', '250F 12 cylinder', 310, null, 'REAR', 1, true, 1),
        ('Maserati', '250F 6 cylinder', 270, null, 'REAR', 1, true, 1),
        ('Maserati', 'Alfieri', 460, 520, 'REAR', 1, true, 1),
        ('Maserati', 'GranTurismo MC GT4', 430, 535, 'REAR', 1, true, 1),
        ('Maserati', 'Levante S', 424, 580, 'ALL', 1, true, 1),
        ('Maserati', 'MC12 GT1', 580, 650, 'REAR', 1, true, 1),
        ('Maserati', 'Quattroporte GTS', 530, 710, 'REAR', 1, true, 1),

        ('Mazda', '787B', 690, 608, 'REAR', 1, true, 1),
        ('Mazda', 'MX5 Cup', 160, 200, 'REAR', 1, true, 1),
        ('Mazda', 'MX5 ND', 160, 200, 'REAR', 1, true, 1),
        ('Mazda', 'Miata NA', 130, 152, 'REAR', 1, true, 1),
        ('Mazda', 'RX-7 Spirit R', 276, 314, 'REAR', 1, true, 1),
        ('Mazda', 'RX-7 Tuned', 444, 398, 'REAR', 1, true, 1),

        ('McLaren', '570S', 562, 600, 'REAR', 1, true, 1),
        ('McLaren', '650S GT3', 500, 500, 'REAR', 1, true, 1),
        ('McLaren', 'F1 GTR', 595, 693, 'REAR', 1, true, 1),
        ('McLaren', 'MP4-12C', 616, 600, 'REAR', 1, true, 1),
        ('McLaren', 'MP4-12C GT3', 500, 500, 'REAR', 1, true, 1),
        ('McLaren', 'P1 GTR', 986, 1000, 'REAR', 1, true, 1),
        ('McLaren', 'P1', 903, 900, 'REAR', 1, true, 1),

        ('Mercedes', 'SLS AMG', 571, 650, 'REAR', 1, true, 1),
        ('Mercedes', 'SLS AMG GT3', 520, 600, 'REAR', 1, true, 1),
        ('Mercedes', '190E EVO II', 370, 310, 'REAR', 1, true, 1),
        ('Mercedes', 'AMG GT3', 520, 600, 'REAR', 1, true, 1),
        ('Mercedes', 'C9 1989 LM', 750, 500, 'REAR', 1, true, 1),

        ('Nissan', '370z Nismo', 350, 374, 'REAR', 1, true, 1),
        ('Nissan', 'GT-R GT3', 600, 700, 'ALL', 1, true, 1),
        ('Nissan', 'GT-R NISMO', 592, 652, 'ALL', 1, true, 1),
        ('Nissan', 'Skyline GTR R34 V-Spec', 325, 392, 'ALL', 1, true, 1),

        ('Pagani', 'Huayra', 730, 1000, 'REAR', 1, true, 1),
        ('Pagani', 'Huayra BC', 740, 1100, 'REAR', 1, true, 1),
        ('Pagani', 'Zonda R', 750, 710, 'REAR', 1, true, 1),

        ('Porsche', '718 Boxter S', 350, 420, 'REAR', 1, true, 1),
        ('Porsche', '718 Boxter S PDK', 350, 420, 'REAR', 1, true, 1),
        ('Porsche', '718 VCayman', 350, 420, 'REAR', 1, true, 1),
        ('Porsche', '718 RS 60 Spyder', 150, 148, 'REAR', 1, true, 1),
        ('Porsche', '908 LH', 350, 320, 'REAR', 1, true, 1),
        ('Porsche', '911 Carrera RSR 3.0', 330, null, 'REAR', 1, true, 1),
        ('Porsche', '911 Carrera S', 420, 500, 'REAR', 1, true, 1),
        ('Porsche', '911 GT1-98', 600, null, 'REAR', 1, true, 1),
        ('Porsche', '911 GT3 Cup 2017', 485, null, 'REAR', 1, true, 1),
        ('Porsche', '911 GT3 R 2016', 500, null, 'REAR', 1, true, 1),
        ('Porsche', '911 GT3 RS', 500, 460, 'REAR', 1, true, 1),
        ('Porsche', '911 R', 500, 460, 'REAR', 1, true, 1),
        ('Porsche', '911 RSR 2017', 510, null, 'REAR', 1, true, 1),
        ('Porsche', '911 Turbo S', 580, 750, 'ALL', 1, true, 1),
        ('Porsche', 'Porsche 917 K', 600, null, 'REAR', 1, true, 1),
        ('Porsche', 'Porsche 917/30 Spyder', 1200, 1200, 'REAR', 1, true, 1),
        ('Porsche', '918 Spyder', 887, 1275, 'ALL', 1, true, 1),
        ('Porsche', '919 Hybrid 2015', 900, null, 'ALL', 1, true, 1),
        ('Porsche', '919 Hybrid 2016', 900, null, 'ALL', 1, true, 1),
        ('Porsche', '935/78 "Moby Dick"', 845, 784, 'REAR', 1, true, 1),
        ('Porsche', '962 C Long Tail', 650, null, 'REAR', 1, true, 1),
        ('Porsche', '962 C Short Tail', 625, null, 'REAR', 1, true, 1),
        ('Porsche', 'Cayenne Turbo S', 570, 800, 'ALL', 1, true, 1),
        ('Porsche', 'Cayman GT4', 385, 420, 'REAR', 1, true, 1),
        ('Porsche', 'Cayman GT4 Clubsport', 385, 420, 'REAR', 1, true, 1),
        ('Porsche', 'Macan Turbo', 400, 550, 'ALL', 1, true, 1),
        ('Porsche', 'Panamera Turbo', 550, 770, 'ALL', 1, true, 1),

        ('Praga', 'R1', 210, 220, 'REAR', 1, true, 1),

        ('RUF', 'CTR Yellowbird', 469, 553, 'REAR', 1, true, 1),
        ('RUF', 'RT12 R', 730, 940, 'REAR', 1, true, 1),
        ('RUF', 'RT12 R AWD', 730, 940, 'ALL', 1, true, 1),

        ('Scuderia Glickenhaus', 'P4/5 Competizione 2011', 450, 500, 'REAR', 1, true, 1),
        ('Scuderia Glickenhaus', 'SCG 003C', 530, 700, 'REAR', 1, true, 1),

        ('Shelby', 'Cobra 427 S/C', 500, 626, 'REAR', 1, true, 1),

        ('Tatuus', 'FA01', 198, 230, 'REAR', 1, true, 1),

        ('Toyota', 'AE86', 122, 142, 'REAR', 1, true, 1),
        ('Toyota', 'AE86 Drift', 185, 206, 'REAR', 1, true, 1),
        ('Toyota', 'AE86 Tuned', 200, 206, 'REAR', 1, true, 1),
        ('Toyota', 'Celica ST185 4WD Turbo', 295, 459, 'ALL', 1, true, 1),
        ('Toyota', 'GT86', 200, 205, 'REAR', 1, true, 1),
        ('Toyota', 'Supra MKIV', 280, 458, 'REAR', 1, true, 1),
        ('Toyota', 'Supra MKIV Drift', 624, 756, 'REAR', 1, true, 1),
        ('Toyota', 'Supra MKIV Time Attack', 690, 756, 'REAR', 1, true, 1),
        ('Toyota', 'TS040 Hybrid 2014', 1000, null, 'ALL', 1, true, 1),

        -- all WRC Generations cars

        ('Toyota', 'GR Yaris Rally 1', 380, 500, 'ALL', 1, false, 3),
        ('Ford', 'Puma Rally 1', 380, 500, 'ALL', 1, false, 3),
        ('Hyundai', 'i20 N Rally 1', 380, 500, 'ALL', 1, false, 3),
        ('Citroën', 'C3 Rally 2', 290, 440, 'ALL', 1, false, 3),
        ('Škoda', 'Fabia Evo Rally 2', 290, 440, 'ALL', 1, false, 3),
        ('Ford', 'Fiesta Rally 2', 290, 440, 'ALL', 1, false, 3),
        ('Hyundai', 'i20 N Rally 2', 290, 440, 'ALL', 1, false, 3),
        ('Volkswagen', 'Polo Gti Rally 2', 290, 440, 'ALL', 1, false, 3),
        ('Lancia', 'Fulvia HF', 165, 172, 'FRONT', 1, false, 3),
        ('Alpine', 'A110', 180, 190, 'REAR', 1, false, 3),
        ('Lancia', 'Stratos HF', 285, 280, 'REAR', 1, false, 3),
        ('Fiat', '131 Abarth', 230, 230, 'REAR', 1, false, 3),
        ('Audi', 'Quattro A1', 300, 420, 'ALL', 1, false, 3),
        ('Lancia', '037', 325, 320, 'REAR', 1, false, 3),
        ('Audi', 'Quattro A2', 370, 450, 'ALL', 1, false, 3),
        ('Peugeot', '205 Turbo 16 Evo 1', 340, 460, 'ALL', 1, false, 3),
        ('Peugeot', '205 Turbo 16 Evo 2', 550, 520, 'ALL', 1, false, 3),
        ('Lancia', 'Delta HF 4WD', 350, 535, 'ALL', 1, false, 3),
        ('Lancia', 'Delta HF Integrale Evoluzione', 350, 535, 'ALL', 1, false, 3),
        ('Toyota', 'Celica Turbo 4WD', 300, 460, 'ALL', 1, false, 3),
        ('Subaru', 'Impreza WRC', 315, 490, 'ALL', 1, false, 3),
        ('Mitsubishi', 'Lancer Evo V', 320, 510, 'ALL', 1, false, 3),
        ('Toyota', 'Corolla', 305, 530, 'ALL', 1, false, 3),
        ('Citroën', 'Xsara WRC', 320, 540, 'ALL', 1, false, 3),
        ('Ford', 'Focus RS WRC', 315, 520, 'ALL', 1, false, 3),
        ('Citroën', 'DS3 WRC', 320, 460, 'ALL', 1, false, 3),
        ('Volkswagen', 'Polo R WRC', 315, 425, 'ALL', 1, false, 3),
        ('Ford', 'Fiesta WRC', 380, 500, 'ALL', 1, false, 3),
        ('Toyota', 'Yaris WRC', 380, 500, 'ALL', 1, false, 3),
        ('Porsche', '911 GT3 RS RGT (997)', 365, 450, 'REAR', 1, false, 3),
        ('Citroën', 'C3 WRC', 380, 500, 'ALL', 1, false, 3),
        ('Ford', 'Fiesta Rally 3', 220, 290, 'ALL', 1, false, 3),
        ('Volkswagen', 'Polo Gti Rally 2', 290, 440, 'ALL', 1, false, 3)
    )
    AS data(brand, name, horse_power, torque, wheel_drive, version, from_dlc, simulator_id);

