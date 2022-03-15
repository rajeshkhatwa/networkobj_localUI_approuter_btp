using common.db.CommonEntities as CommonObj from '../db/schema/Common';
using masterData as MD from '../db/schema/masterData';
using FDC as FDC from '../db/schema/FDC';


/*Refer https://github.com/SAP-samples/hana-opensap-cloud-2020/blob/main/db/schema/purchaseOrder.cds */

service MasterDataService @(path : '/MasterDataService')
{
 entity Mediums as projection on CommonObj.Medium;
 entity Dimensions as projection on CommonObj.Dimension;
 entity Measurements  as projection on CommonObj.Measurement;
 entity NetworkObjectTypes as projection on CommonObj.NetworkObjectTypes;

 view ProductionNetworks as select from MD.ProductionNetworks;
 view ProductionNetworksVH  @(cds.redirection.target:false) as select from MD.ProductionNetworksVH;

 entity NetworkObjects as projection on MD.NetworkObjects;


 
}


service FDCService  @(path : '/FDC')
{
    entity WCTests as projection on FDC.WellCompletionTests {
      *, items : redirected to WCTestsItems
    };
    entity WCTestsItems as projection on FDC.WCTestsItems {
   * , parent : redirected to WCTests, measurementProperty : redirected to Measurements
    };
   entity Measurements  as projection on CommonObj.Measurement;

   view Worklist as select from FDC.WCTestWorklist;
   

}

annotate MasterDataService with @(requires: 'authenticated-user') ;
annotate MasterDataService.NetworkObjects with @( restrict: [
  { grant: ['READ','WRITE'], to: 'MasterDataAdmin'},
  { grant: ['READ','WRITE'], where: 'country_code = $user.countryassigned' },
]);

annotate MasterDataService.Mediums with @( restrict: [
  { grant: ['READ','WRITE'], to: 'MediumAdmin'},

]);


annotate MasterDataService.NetworkObjects with @(UI : {
    HeaderInfo        : {
        $Type          : 'UI.HeaderInfoType',
        TypeName       : '{i18n>networkObject}',
        TypeNamePlural : '{i18n>networkObjects}',
        Title          : {Value : networkObjectID},
        Description    : {Value : networkObjectDescription}
    },

    SelectionFields   : [
        networkObjectID,
        networkObjectType_networkObjectType,
        productionNetwork,
    // country_code
    ],
    LineItem          : [

        {Value : networkObjectID},
        {Value : networkObjectDescription},
        {Value : networkObjectType_networkObjectType},

        /*       Trying to fetch from the association  .. When associated field is to be fetched..then ensure that it's the navigation and then property
                 in the metadata for the entity NetworkObjects, it;s networkObjectType---> Navigation
                also in Manifest.json  file set autoExpandSelect": true, */

        {
            $Type : 'UI.DataField',
            Value : networkObjectType.description
        },

        {
            Value : country_code,
            Label : 'Country'
        },
        {Value : country.name},
        {Value : productionNetwork}

    ],
    Facets            : [ // if I had used HeaderFacets, then this info would have been displayed in the header Section itself
        {
            $Type  : 'UI.ReferenceFacet',
            Label  : '{i18n>details}',
            Target : '@UI.FieldGroup#Main'
        },
        {
            $Type  : 'UI.ReferenceFacet',
            Label  : '{i18n>Admin}',
            Target : '@UI.FieldGroup#Admin'
        }

    ],

    FieldGroup #Main  : {Data : [
        {Value : networkObjectID},
        {Value : networkObjectDescription},
        {Value : networkObjectType_networkObjectType},
        {Value : networkObjectType.description},
        {Value : productionNetwork}
    ]},
    FieldGroup #Admin : {Data : [
        {Value : createdAt},
        {Value : createdBy},
        {Value : modifiedAt},
        {Value : modifiedBy}        

    ]}
}

);