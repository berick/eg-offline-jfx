
/*
 * SQLITE offline transaction schema
 */

CREATE TABLE IF NOT EXISTS xact (
    id INTEGER PRIMARY KEY,
    real_time TEXT DEFAULT (DATETIME()) NOT NULL,
    export_time TEXT,
    action TEXT,
    due_date TEXT,
    backdate TEXT,
    item_barcode TEXT,
    noncat_type TEXT,
    noncat_count TEXT,
    patron_barcode TEXT,
    workstation TEXT
);


/*
 * An offline client can have multiple host+workstation combinations.
 */
CREATE TABLE IF NOT EXISTS config (
    is_default INT DEFAULT 0,
    hostname TEXT,
    workstation TEXT,
    org_unit INT
);

