INSERT INTO dhikr_programs (key, default_target, premium, sort_order) VALUES
    ('subhanallah',   33, false, 1),
    ('elhamdulillah', 33, false, 2),
    ('allahuekber',   34, false, 3);

INSERT INTO dhikr_program_translations (program_id, locale, name, description)
SELECT id, 'tr', 'Sübhânallah', 'Allah’ı her türlü eksiklikten tenzih etmek'  FROM dhikr_programs WHERE key = 'subhanallah'
UNION ALL SELECT id, 'en', 'SubhanAllah', 'Glory be to Allah'                  FROM dhikr_programs WHERE key = 'subhanallah'
UNION ALL SELECT id, 'tr', 'Elhamdülillah', 'Allah’a hamd etmek'               FROM dhikr_programs WHERE key = 'elhamdulillah'
UNION ALL SELECT id, 'en', 'Alhamdulillah', 'All praise is due to Allah'       FROM dhikr_programs WHERE key = 'elhamdulillah'
UNION ALL SELECT id, 'tr', 'Allâhu Ekber', 'Allah en büyüktür'                 FROM dhikr_programs WHERE key = 'allahuekber'
UNION ALL SELECT id, 'en', 'Allahu Akbar', 'Allah is the greatest'            FROM dhikr_programs WHERE key = 'allahuekber';
