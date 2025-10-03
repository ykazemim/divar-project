#!/bin/bash
echo "API Test Script"
echo "==============="
echo "1. Health check:"
curl -s http://localhost:8080/health
echo -e "\n\n2. Find fraudsters:"
curl -s "http://localhost:8080/users?category=fraudsters" | head -c 300
echo -e "\n\n3. Get user profile:"
curl -s http://localhost:8080/users/user_756 | head -c 200
echo -e "\n\n4. Add tag:"
curl -s -X POST -H "Content-Type: application/json" -d '{"tag":"test_tag"}' http://localhost:8080/users/user_756/tags
echo -e "\n\n5. Remove tag:"
curl -s -X DELETE http://localhost:8080/users/user_756/tags/test_tag
echo -e "\n\nDone!"
