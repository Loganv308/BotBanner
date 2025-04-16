CREATE TABLE IF NOT EXISTS IPInformation (
    id SERIAL PRIMARY KEY,
    IpAddress TEXT NOT NULL,
    CurrentDate TIMESTAMP WITH TIME ZONE,
    Hostname TEXT NOT NULL,
    City TEXT NOT NULL,
    Region TEXT NOT NULL,
    Country TEXT NOT NULL,
    Coords TEXT NOT NULL,
    Organization TEXT NOT NULL,
    Zip INTEGER NOT NULL,
    Timezone VARCHAR(8)
);