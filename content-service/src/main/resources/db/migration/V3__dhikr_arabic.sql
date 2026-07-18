ALTER TABLE dhikr_programs ADD COLUMN arabic VARCHAR(64);

UPDATE dhikr_programs SET arabic = 'سُبْحَانَ اللَّهِ' WHERE key = 'subhanallah';
UPDATE dhikr_programs SET arabic = 'الْحَمْدُ لِلَّهِ' WHERE key = 'elhamdulillah';
UPDATE dhikr_programs SET arabic = 'اللَّهُ أَكْبَرُ' WHERE key = 'allahuekber';

ALTER TABLE dhikr_programs ALTER COLUMN arabic SET NOT NULL;
