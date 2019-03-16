private String getRequestUrl(LatLng origin, LatLng dest) {
//Value of origin
    String str_org = "origin"+origin.latitude+","+origin.longitude;
    //Value of destination
    String str_dest = "destination"+dest.latitude+","+dest.longitude;
    //mode for direction
    String mode = "mode=driving";
    String param = str_org+"&"+str_dest+"&"+mode;
    String output = "json";
    String url ="https://maps.googleapis.com/maps/api/directions"+output+"?"+param;
    return url;

}
private String requestDirections( String reqUrl) throws IOException {
        String responseString = "";
    InputStream inputStream = null;
    HttpURLConnection httpURLConnection = null;
    try{
        URL url = new URL(reqUrl);
        httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.connect();

        //get the response result
        inputStream = httpURLConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        while((line = bufferedReader.readLine())!= null){
            stringBuffer.append(line);

        }

        responseString = stringBuffer.toString();
        bufferedReader.close();
        inputStreamReader.close();

    } catch (Exception e) {
        e.printStackTrace();
    }
    finally {
        if (inputStream != null){
            inputStream.close();
        }

        httpURLConnection.disconnect();
    }
    return responseString;

}










































public  class TaskRequestDirections extends AsyncTask<String, Void, String>{

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //parse json here
        TaskParser taskParser = new TaskParser();
        taskParser.execute(s);

    }

    @Override
    protected String doInBackground(String... strings) {
        String responseString = "";
        try {
            responseString = requestDirections(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;

    }


}
public  class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >
{

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
        JSONObject jsonObject = null;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jsonObject = new JSONObject(strings[0]);
            DirectionsParser directionsParser = new DirectionsParser();
            routes = directionsParser.parse(jsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
        //Get list route and display it into the map

        ArrayList points = null;
        PolylineOptions polylineOptions = null;

        for(List<HashMap<String, String>> path: lists){
            points = new ArrayList();
            polylineOptions = new PolylineOptions();

            for (HashMap<String, String> point: path) {

                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lon"));

                points.add(new LatLng(lat,lon));

            }
            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(Color.BLUE);
            polylineOptions.geodesic(true);


        }

        if(polylineOptions!= null){
            mMap.addPolyline(polylineOptions);

        }
        else{
            Toast.makeText(getApplicationContext()," Direction not found", Toast.LENGTH_SHORT).show();

        }
    }
}