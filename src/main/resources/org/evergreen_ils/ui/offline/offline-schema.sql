
/*
 * SQLITE offline transaction schema
 */

CREATE TABLE IF NOT EXISTS xact (
    id INTEGER PRIMARY KEY,
    realtime TEXT DEFAULT (DATATIME()) NOT NULL,
    action TEXT,
    due_date TEXT,
    backdate TEXT,
    item_barcode TEXT,
    patron_barcode TEXT,
    workstation TEXT
);


