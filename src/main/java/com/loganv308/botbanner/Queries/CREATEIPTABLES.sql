CREATE TABLE IPInfo (
    id SERIAL PRIMARY KEY,
    CurrentDate TIMESTAMP WITH TIME ZONE,
    Hostname TEXT NOT NULL,
    City TEXT NOT NULL,
    Region TEXT NOT NULL,
    RegionName TEXT NOT NULL,
    Country TEXT NOT NULL,
    CoordsLat TEXT NOT NULL,
    CoordsLong TEXT NOT NULL,
    Organization TEXT NOT NULL,
    Zip INTEGER NOT NULL,
    Timezone VARCHAR(8)
    IpAddress TEXT NOT NULL,  
);