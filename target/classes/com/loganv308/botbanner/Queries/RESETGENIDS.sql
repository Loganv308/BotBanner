SELECT setval('users_id_seq', (SELECT MAX(id) FROM IPInformation));
