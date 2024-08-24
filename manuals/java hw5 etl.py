import csv

def valid_zipcode(zipc):
    return len(zipc) == 5 and zipc.isdigit()
state_abbreviation_map = {
    "alabama": "AL",
    "alaska": "AK",
    "arizona": "AZ",
    "arkansas": "AR",
    "california": "CA",
    "colorado": "CO",
    "connecticut": "CT",
    "delaware": "DE",
    "florida": "FL",
    "georgia": "GA",
    "hawaii": "HI",
    "idaho": "ID",
    "illinois": "IL",
    "indiana": "IN",
    "iowa": "IA",
    "kansas": "KS",
    "kentucky": "KY",
    "louisiana": "LA",
    "maine": "ME",
    "maryland": "MD",
    "massachusetts": "MA",
    "michigan": "MI",
    "minnesota": "MN",
    "mississippi": "MS",
    "missouri": "MO",
    "montana": "MT",
    "nebraska": "NE",
    "nevada": "NV",
    "new hampshire": "NH",
    "new jersey": "NJ",
    "new mexico": "NM",
    "new york": "NY",
    "north carolina": "NC",
    "north dakota": "ND",
    "ohio": "OH",
    "oklahoma": "OK",
    "oregon": "OR",
    "pennsylvania": "PA",
    "rhode island": "RI",
    "south carolina": "SC",
    "south dakota": "SD",
    "tennessee": "TN",
    "texas": "TX",
    "utah": "UT",
    "vermont": "VT",
    "virginia": "VA",
    "washington": "WA",
    "west virginia": "WV",
    "wisconsin": "WI",
    "wyoming": "WY"
}
def load_zipcodes():
    zipdata = []
    statedata = []
    citydata = []
    countydata = []
    state_to_id = dict()
    city_to_id = dict()
    county_to_id = dict()
    citystate_to_zip = dict()
    with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/zip_codes_states.csv") as zipcodefile:
        reader = csv.DictReader(zipcodefile, delimiter=',')

        i = 0
        state_index = 0
        city_index = 0
        county_index = 0
        for row in reader:
            zip = row['zip_code']
            x = row['latitude']
            y = row['longitude']
            if x == '':
                x = '0'
            if y == '':
                y = '0'
            state = row['state']
            if state not in state_to_id:
                state_to_id[state] = state_index
                state_index += 1

                strow = {'stateid' :state_to_id[state], 'state':state}
                statedata.append(strow)

            city = row['city']
            if city not in city_to_id:
                city_to_id[city] = city_index
                city_index+=1
                cirow = {'cityid' : city_to_id[city], 'city': city}
                citydata.append(cirow)

            county=row['county']
            if county not in county_to_id:
                county_to_id[county] = county_index
                county_index += 1
                corow={'countyid':county_to_id[county], 'county' : county}
                countydata.append(corow)
            
            citystate_to_zip[(city,state)] = zip
            if city == 'Bronx': print(citystate_to_zip[(city,state)])
            new_row = {'zip' : int(zip), 'x':float(x), 'y':float(y), 'stateid':state_to_id[state],'cityid':city_to_id[city],'countyid':county_to_id[county]}
            zipdata.append(new_row)


    # fill tables
    with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/zipcodes.csv",'w') as zip_csv, open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/states.csv",'w') as states_csv, open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/cities.csv",'w') as cities_csv, open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/counties.csv",'w') as counties_csv:
        fieldnames1 = ['zip','x','y','stateid','cityid','countyid']
        zip_out = csv.DictWriter(zip_csv,fieldnames=fieldnames1)
        zip_out.writeheader()

        fieldnames2 = ['stateid','state']
        states_out = csv.DictWriter(states_csv,fieldnames=fieldnames2)
        states_out.writeheader()

        fieldnames3 = ['cityid','city']
        cities_out = csv.DictWriter(cities_csv,fieldnames=fieldnames3)
        cities_out.writeheader()

        fieldnames4 = ['countyid','county']
        counties_out = csv.DictWriter(counties_csv,fieldnames=fieldnames4)
        counties_out.writeheader()
        for row in zipdata:
            zip_out.writerow(row)
        for row in statedata:
            states_out.writerow(row)
        for row in citydata:
            cities_out.writerow(row)
        for row in countydata:
            counties_out.writerow(row)



    # now load farmers markets
    products_to_id = dict()
    payment_to_id = dict()
    fmdata = []
    paymentdata = []
    productdata = []
    with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/payment_methods.csv") as pmfile:
        reader = csv.DictReader(pmfile, delimiter=',')
        for row in reader:
            payment_to_id[row['paymentname']] = int(row['paymentid'])
    with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/products.csv") as pmfile:
        reader = csv.DictReader(pmfile, delimiter=',')
        for row in reader:
            products_to_id[row['productname']] = int(row['productid'])
    with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/Export.csv") as fmfile:
        print("payment keys: " + str(payment_to_id.keys()))
        print("products keys: " + str(products_to_id.keys()))
        reader = csv.DictReader(fmfile, delimiter=',')
        for row in reader:
            fmid=int(row['FMID'])
            name=row['MarketName']
            if ',' in name:
                name = name.replace(","," ")
            zip = row['zip']
            city = row['city']
            if ',' in city: city = city.replace(","," ")
            state=row['State']
            if ',' in state: city = city.replace(","," ") 
            street=row['street']
            if ',' in street:
                street = street.replace(","," ")
            
            x = row['x']
            y = row['y']
            if x == '': x = 0.0
            if y == '': y = 0.0
            if len(zip) > 5:
                zip = zip[:5]
            if zip == '' or len(zip) < 5:
                print((city.strip(),state.lower()))
                if state.lower() in state_abbreviation_map and (city.strip(), state_abbreviation_map[state.lower()]) in citystate_to_zip:
                    zip = citystate_to_zip[(city.strip(),state_abbreviation_map[state.lower()])]
                    print(zip)
                else:
                    zip = 0
            data = {'fmid':int(fmid), 'marketname':name, 'street':street,'marketx':float(x), 'markety':float(y), 'zip':int(zip)}
            fmdata.append(data)


            
            for payment_type in payment_to_id.keys():
                if row[payment_type] == 'Y':
                    # print('here')
                    d = {'fmid':fmid, 'paymentid':payment_to_id[payment_type]}
                    paymentdata.append(d)

            
            for product in products_to_id.keys():
                if row[product] == 'Y':
                    # print('here2')
                    d = {'fmid':fmid, 'productid':products_to_id[product]}
                    productdata.append(d)


    with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/markets.csv",'w') as out:
        fieldnames=['fmid','marketname','street','marketx','markety','zip']
        fmout = csv.DictWriter(out,fieldnames=fieldnames)
        fmout.writeheader()
        for row in fmdata:
            fmout.writerow(row)

    with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/market_payments.csv",'w') as out:
        fieldnames=['fmid','paymentid']
        fmout = csv.DictWriter(out,fieldnames=fieldnames)
        fmout.writeheader()
        for row in paymentdata:
            fmout.writerow(row)
    with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/market_products.csv",'w') as out:
        fieldnames=['fmid','productid']
        fmout = csv.DictWriter(out,fieldnames=fieldnames)
        fmout.writeheader()
        for row in productdata:
            fmout.writerow(row)
            
        


if __name__ == "__main__":
    load_zipcodes()
    
    # markets = {
    #     'fmid' : [],
    #     'marketname' : [],
    #     'marketx' : [],
    #     'markety' : [],
    #     'street' : [],
    #     'zip' : []
    # }

    # state_to_id = dict()
    # states = {
    #     'stateid' : [],
    #     'statename' : []
    # }

    # city_to_id = dict()
    # cities = {
    #     'cityid' : [],
    #     'cityname' : []
    # }

    

    

    # product_to_id = dict()
    # products = {
    #     'productid' : list(range(30)),
    #     'productname' : ['Organic', 'Bakedgoods', 'Cheese', 'Crafts', 'Flowers', 'Eggs', 
    #           'Seafood', 'Herbs', 'Vegetables', 'Honey', 'Jams', 'Maple', 
    #           'Meat', 'Nursery', 'Nuts', 'Plants', 'Poultry', 'Prepared', 
    #           'Soap', 'Trees', 'Wine', 'Coffee', 'Beans', 'Fruits', 'Grains', 
    #           'Juices', 'Mushrooms', 'PetFood', 'Tofu', 'WildHarvested']
    # }
    

    # market_products = {
    #     'fmid' : [],
    #     'products_productid' : []
    # }

    # payment_method_to_id = dict()

    # payment_methods = {
    #     'paymentid' : [0,1,2,3,4],
    #     'paymentname' : ["credit", "WIC", "WICcash","SFMNP","SNAP"]
    # }

    # market_payments = {
    #     'fmid' : [],
    #     'paymentid' : []
    # }

    # with open("/mnt/c/ProgramData/MySQL/MySQL Server 8.0/Uploads/Export.csv") as fm:
    #     pass
    
    
    #     # i = 0
    #     # for row in fm_data.iterrows():
    #     #     if i == 0:
    #     #         i+=1
    #     #         continue
    #     #     # TODO: change these all to be NULL if its an empty string ''
    #     #     fmid = int(row['FMID'])
    #     #     marketname = row['MarketName']
    #     #     street = row['street']
    #     #     city = row['city']
    #     #     county = row['County']
    #     #     state = row['State']
    #     #     zip = int(row['zip'])
    #     #     x = float(row['x'])
    #     #     y = float(row['y'])
    #     #     payments_info = [row[key] for key in payment_methods['paymentname']]
    #     #     products_info = [row[key] for key in products['productname']]


            
    #     #     i += 1






