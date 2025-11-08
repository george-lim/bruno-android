package com.bruno.android.routing;

import com.bruno.android.location.Coordinate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {@link RouteGenerator} mock implementation that returns cached paths.
 */
public class MockRouteGeneratorImpl extends RouteGenerator {

    private int currentIndex = 0;

    private static final String[] mockResponses = {
            "{'routes':[{'polyline':{'encodedPolyline':'i~kcF~ochVC`AN@b@xD[J?LUFONFb@ClAItA~CCdALAbC@@KtA[bBSh@LLN^H`@Vl@xAMGkCFjCyAL[y@G]Wa@CCX}@V}AHgAAA@cCeAMuD@uCIyBAMCKQOe@wBe@q@KkAp@w@f@UFcAAc@F_DJw@WcAIs@M_@@sANk@AGEe@?s@Hs@Te@AeA?k@GO@?dAhF?~BXhABhBXzGShB_@`@D^TJNH\\\\?FCWEMWYSKa@EoB^wGR}B[}@CuBWiF??eARAh@FvA?V@t@WfAIH@PF`@?rAOh@?t@Nv@Dv@V~CKb@GbA@f@SxA{@VQrB`@t@NRl@LJx@BfA?jDJLcCAs@EONOTG?MZKc@yDOABaA'}}]}",
            "{'routes':[{'polyline':{'encodedPolyline':'i~kcF~ochVDw@JCCm@e@oC?_@A]Vy@f@_AVa@bACv@BJ^Dj@Cd@MAKLXYFwCOCSEK?IEa@GeAAqANUCDeAI@m@KUQQq@Cc@EiBWm@U[k@gCAcB@QOA?]R??e@]AAiBG{B?mFE??eUB_@YCg@w@mB@G?@uGcBAC]P[HmAKSEgAAwCQA{ANcEA_ALyDl@}@AqC_@eAMeAYwASCCkJ{@gAUqDa@qBKkB@iCJi@AqMj@yET{CAw@?cCReGZe@C|@BfIg@h@GjDB`DMbOs@l@AhA?nDMhABpDZlANfATjJz@PCfBDlAR|Cl@fADrAQvCe@tF?`ADMIc@I}@]kAAIBWCa@?qCNkBLkAHUEUWI[A_@f@qErAmME]Q{@FeAl@qFTmA@KJCtOEPGx_@?FgG@cH`E@aEAAxHGpFy_@?QFuODKBIj@UrAk@vF?`@RbAA^iBdQMnABb@JTVRXBpCUdCKf@Ef@?PBHClABhA^\\\\HDDP@BjEDXHLIlAQZFb@@fB?hDfEAH?f@v@Lb@F??xUD??fGHxB?|@\\\\@?d@S??\\\\N@AP@bBj@fC^l@LZDxBBRPp@TPl@JHAEdATB`BOt@@`@FHD\\\\BFBH@GvCYXNOHB@u@Ee@IUw@CcABq@fA[x@IX@\\\\?^d@nCBl@KBEv@'}}]}",
    };

    @Override
    public void generateRoute(final OnRouteResponseCallback callback,
                              final Coordinate origin,
                              double totalDistance,
                              double rotation) {
        try {
            callback.onRouteReady(parseRouteSegmentsFromJson(new JSONObject(mockResponses[currentIndex])));
            currentIndex = (currentIndex + 1) % mockResponses.length;
        } catch (JSONException e) {
            callback.onRouteError(new RouteGeneratorException(e));
        }
    }
}
