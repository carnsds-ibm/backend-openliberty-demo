echo "==== TRAFFIC GENERATING NOW ===="

handler()
{
  echo "==== ENDING TRAFFIC NOW ====";
	exit 1;
}
trap handler INT

# Make a user if needed
curl -s --header "Content-Type: application/json" --request POST --data '{"userName":"someuser", "password":"cGFzc3dvcmQK"}' $GATEWAY/User/Create

for i in {1..100}
do
curl -s $GATEWAY > /dev/null
sleep 0.1
echo "=== SIGNING IN ==="
RES=$(curl -s --header "Content-Type: application/json" --request POST --data '{"userName":"someuser", "password":"cGFzc3dvcmQK"}' $GATEWAY/User/Login)
TOKEN=$(echo $RES | jq -r '.key')
echo "=== SIGNING OUT ==="
sleep 0.1
STAT=$(curl -s --header "Content-Type: application/json" --request POST --data '{ "key":"'$TOKEN'" }' $GATEWAY/User/Logout)
sleep 0.1
curl -s $GATEWAT/Calc
sleep 0.1
done
echo "==== ENDING TRAFFIC NOW ====";
