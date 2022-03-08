
/*
 * SQLITE offline transaction schema
 */

CREATE TABLE IF NOT EXISTS xact (
    id INTEGER PRIMARY KEY,
    realtime TEXT DEFAULT (DATETIME()) NOT NULL,
    action TEXT,
    due_date TEXT,
    backdate TEXT,
    item_barcode TEXT,
    noncat_type TEXT,
    noncat_count TEXT,
    patron_barcode TEXT
);


