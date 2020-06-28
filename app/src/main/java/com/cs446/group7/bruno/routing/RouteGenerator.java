package com.cs446.group7.bruno.routing;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouteGenerator {
    private Context context;
    private final String gMapsApiKey;

    private static final String DIRECTIONS_ENDPOINT = "https://maps.googleapis.com/maps/api/directions/";
    private static final String TAG = "RouteGenerator";

    private static final String[] mockPaths = {
            "__`iGvjgeN]s@]i@_@e@g@a@gAc@}@OH_AJq@dAyBt@aB\\i@ZONE\\@TDXRt@z@u@{@QMBMPy@b@iBH]JD\\wARk@d@}@vAtAbAlAlEdF~AfBFP?VKNi@h@SRm@z@Zf@Rf@Rp@H`@j@ULGhAgAJ?HBHJBTCUIKICK?iAfAMFk@TF\\Hl@J|@sBn@QL]`@s@hAODMAKKyAiBKP?@A@_B`DqCkD_@q@",
            "u~_iGfkgeNg@cA]i@_@e@g@a@gAc@cAQGv@CDIBCAAl@?RNKJON{@Ig@N{AF[bAsBXb@d@Vx@d@p@n@RXLHFEBGJSf@w@g@v@KRCFGDMISYq@o@_B}@Yc@}@fBGPKp@I~@|@Nf@P^Pf@`@^d@\\h@f@bA",
            "u~_iGfkgeNg@cA]i@_@e@g@a@gAc@cAQGv@CDIBCAAl@?RWPOv@QjAETILODSEQM]KgAQ[A?hBA|@K`AQlAk@|AOZUJmBnD_@h@iEkFu@vAOQiAwA_@h@IPHQ^i@hAvANPt@wAtDrEz@kB@OCOeBoBLWBk@uA_BgE}EYYX_@fAaBrB}Cp@gALOGO?A?GAG?CDEBMEYIBIEwCkDiAcBHC^M^U\\]NSh@{@vBtC|@`A~@hAnAtAhCpCf@^n@`@f@Rp@PhC\\fAb@f@`@^d@\\h@f@bA",
            "u~_iGfkgeNg@cA]i@_@e@g@a@gAc@iC]q@Qg@So@a@g@_@iCqCoAuA_AiA}@aA{CaE]m@]gAKm@YgD]aFM_AKc@[}@yF}Jm@o@mAs@GEKb@g@`CERA?A?c@WSR_@PuB`Dx@xA@LCPuAnBO@{AyBQMa@O`@NPLzAxBNAtAoBBQAMy@yAtBaD^QRSb@V@?n@uCJc@FD~@kDb@}Az@eCv@iBr@wApB{C|C_E|@mApDkF~BmD_C}Bo@[w@Yo@a@kAgAeAkAcAiAgE_FyBaC}@}@OLMNkAbC_BdDk@jAcAaB{BcC}AaBxEdFbA`Bf@p@PXdDjDhBjB~BoFtAaC^g@dAuBrAkCD@F?JEHMBQ?IAGlBqDlLaU~BgER[nA{B`AqBbAmCbAbAPTl@eA`BqC{AgBzAfBdD~DVRXL\\DbDSzCSNGNKb@k@~CgFJYBM^EP}@d@XZB`Af@nAv@j@d@t@x@zIpLvDbFlBxCnAeBtEwHvEfHwEgHuEvHoAdB`AdBnAxBn@|@|BvC~E|F^X`@Vb@L~Bh@~@d@v@p@fEbGp@fAZn@`@~@\\fAThAV|BNjA\\lAXt@Zl@h@v@|@jAxExFZb@b@x@b@fANn@n@lAdAfBqAdBcEpGkLbQ_@p@Sb@g@x@Ol@[vASj@]r@gAjB_CmBMIOh@Ij@I`A]zAOv@F^VfAATM`@SDQNaApBa@e@sAkAsJoIoFyEcDsCi@k@y@}@u@iAiA}BkD{Ig@cAy@oAqEaG[GkB{Ba@i@aIxLoBxCq@dAaD|EqFnImGrJcDxE_BfCyA|BQVTTb@d@hCpCvA`Az@ZbBTbAPf@P^Pf@`@^d@\\h@f@bA",
            "u~_iGfkgeNl@`AlBzBnArA}BtEyAvCiC}CmBwBiAsAy@k@]|@QXPY\\}@i@Y_@IBYNeA@QJ@NEHMDUPkANw@l@c@DIN{@Ig@|@Nf@P^Pf@`@^d@\\h@f@bA",
            "u~_iGfkgeNg@cA]i@_@e@g@a@gAc@iC]q@Qg@So@a@g@_@iCqCoAuA_AiA}@aAwBuCi@z@]b@_@X_@PYHYBm@EaC[cAKw@@k@JOFe@XYXeAvAO\\s@~@o@t@c@VSHeBXoB`@q@^YXiCjDsBzC}@qAu@mAgBiDiAuAeEpG{AzBOSC?WHASaBsBgD_EQj@Sl@Rm@Pk@S[a@e@u@q@YGCQaBiBk@c@_Aa@Hw@Bm@CoCIeE@yDJmJDa@R{@Zw@Zk@W_@eA]UOKOWKa@KMC_@GUYo@u@GUA[TERUb@{@Pk@@i@a@gDMkDE{@G]o@y@CAQKl@kA|@gBqEoFgAqAeFwF]c@PY`A_BHNbA|AMLEN?T?B?C?UDOVW`@m@bAyAPUBBhAAND~@{Aj@r@bA_BRZRPRBLCHEvCkElC{D~CwEfCqDPVjDoFlAiBjBmC`DyE|AcCz@iAjBkCPEZGJ@RHLFNZRZVZpFnGRFrArAR_@FY`AiBh@cArB|BAOH?I?Bv@@fA[l@OLUDMRk@dAYp@wBpEc@`Al@r@fBlB|BhCjCpCdBjBpDjEdCjCj@f@n@`@fBt@rCnCf@l@l@b@f@^d@Zd@RjA`@|@NjAJl@@lDZ`BXdAVrAd@x@Z`Bt@lBdAp@f@pA~Ab@\\`AlAt@dA^h@tGoJ`EaGJf@Jp@@JAKKq@Kg@wMpRmQlX_MfRgC|DcDxEyDdGQVTTb@d@hCpCf@^n@`@f@Rp@PhC\\fAb@f@`@^d@\\h@f@bA",
            "u~_iGfkgeNg@cA]i@_@e@g@a@gAc@iC]q@Qg@So@a@g@_@iCqCc@e@UUPWxA}B~AgCbDyEfC}DzI}MbBiCjCaEnByC`IyL`@h@jBzBSZy@fAkAhAVj@\\d@XRD~@LAJLHTDf@N~BJj@J^LRZh@kAfB_@h@UNUJMBDr@FXLPp@t@q@u@MQGYEs@LCTKTO|BeD|EyHf@bAjDzIhA|Bt@hAx@|@h@j@bDrCnFxErJnIrAjA`@d@fIzKb@f@j@f@tAn@x@Pv@Jr@DlDGAvBB~AExAGt@[jBe@zA]r@_BnC{DjHw@rAqAzBm@v@_@e@mAwAsCiDiDcEmBsBa@a@aDoCwBcB]OW?MEcCOeAC_@DGDFE^EdABbCNLDK|JQQ_Au@MGS@yEd@WFOLy@hA[n@EXYAs@Q[?e@POZ]h@_@`@UJKPmCjEyCoEaAoA_LqMmDeEaAcA{BkCm@aA",
            "u~_iGfkgeNg@cA]i@_@e@g@a@gAc@iC]q@Qg@So@a@g@_@iCqCoAuA_AiA}@aA{CaE]m@]gAKm@YgD]aFM_AKc@[}@yF}Jm@o@mAs@a@[a@a@kFiGm@m@c@]cAe@m@OiC]UGy@c@uBwB}@aAYn@OASCQCGAKGUd@c@c@oAfC[b@a@\\]Tm@P_BN_ARODs@kEmAv@lAw@r@jENE~@S~AOl@Q\\U`@]Zc@nAgCb@b@Te@JFF@PBb@DXo@|@`AtBvBx@b@TFhC\\l@NbAd@b@\\l@l@jFhG`@`@`@ZTLdA{EbA}Cp@cB|@eBr@aA~DoFr@_AxAoBz@gAn@aAx@oAfBqCf@l@dAv@|@l@x@ZbAXtAPz@FrBJzBVN{CL}@BWSy@_@m@AMDOHa@A_@Nc@NWVUVMf@m@CSDQbBaCnB}C|AkCFED@RRfJdLgInKU^M^o@`Dk@pCrAr@RJj@b@`@b@z@dALd@lCvDNTaIxLoBxCq@dAaD|EqFnImGrJcDxE_BfCyA|BQVTTb@d@hCpCvA`Az@ZbBTbAPf@P^Pf@`@^d@\\h@f@bA",
            "u~_iGfkgeNg@cA]i@_@e@g@a@gAc@iC]q@Qg@So@a@g@_@iCqCoAuA_AiA}@aAwBuC`@s@T[VSj@YlCw@mCv@k@XWRUZa@r@vBtC|@`A~@hAhBsClGaJ`LeQvEkHQa@OK{ASu@Ks@WSOGKuAqAGSUSsEoErEnETRFR|@x@L?h@ZZLh@HlCXZ?XCNGnBmCf@y@zHgLgAaBfA`Bp@~@jBzBZFzBvCtAhBx@nAf@bAP`@\\g@VOTIhBMv@UdAS@IjAoBNKPFhCvCsAhCER@PTZvDrELRLVq@~@y@|@}AlBQ^[t@xApAjB`B|EfExIxHh@f@~ChE~DnFp@p@h@\\lAf@|@PbAHb@@dBEv@AAj@BfB?jBInAOjAa@dBa@fAgAnB}B`EmBnDcBzCq@`AY\\MQmAyAaAkAsCgDkEcFwBqBsD}Ck@a@]OW?MEyDU]B]JWNi@b@{AtA[N_@DYEqAiApAhAXD^EZOzAuAh@c@VO\\K\\CxDTLDIrGAhBMMg@a@a@[OCs@D_Ed@QHw@dAQVWp@AJO@i@Oa@Ga@HYR]r@_@b@WLKJc@v@c@r@kAhBaBcCU_@eAyAkBwBaCuC}JkLaAcAkAwA{@eAa@o@",
            "u~_iGfkgeNg@cA]i@_@e@g@a@gAc@iC]q@Qg@So@a@g@_@iCqCc@e@UUPWxA}B~AgCbDyEfC}DzI}MbBiCjCaEnByC`IyL`@h@jBzBZFzBvCtAhBx@nAx@dBnD`Jt@xA{AbCMVCP?p@Kj@Wd@a@l@]r@k@~AnFzFfAlAhHzHv@x@ZgA~C_Kl@}BfA`ApE|DrAjA`@d@fIzKb@f@j@f@tAn@x@Pv@Jr@DlDGAvBB~AExAGt@[jBe@zA]r@_BnC{DjHw@rAqAzBm@v@g@d@uCxBYw@MSaAw@WQm@g@]~@MVILw@bAo@k@uEwD_ByAUMOEQAi@?KrFE`C[pRU`JcGSw@GgASa@KCNCJeAX[b@Sf@_@_@Uh@w@vAsPwRmDdHaAzBwDkEmEuEe@Wu@S_@O[W_@a@eDmEbCeEaAyAEQ?OBKt@kARWFCAeA?Y@MDMBEOQ^s@bDsGjEuIbAsBaAcAkAwA{@eAYc@GK",
            "y}_iGnlgeNcAkB]i@_@e@g@a@_@Qg@QcAQcBU{@[wAaAmDwDUUPWxA}B~AgCbDyElGsJpFoIbI}L`IyL`@h@jBzBZFzBvCtAhBx@nAf@bAjDzIjA`C{AbCMVCn@Bo@LW|AcCp@dAx@|@h@j@bDrCnFxErJnIrAjA`@d@jA_CPC^FRLrAfBtBD|@KP@TJPDj@CNEV@R?LCCPAPJLFFEHKZWh@Ob@ADtA`@FKH?DD@Hj@AJ@FHBP?|CEHIBcABIBKJCnBAV^Bb@@dBEv@AAj@BfB?jBInAOjAa@dBa@fAgAnB}B`EmBnDcBzCq@`AY\\~BlCDFEG_CmCONwBbBuBbBw@r@g@l@i@|@a@`AUt@_@fBmBw@[SoC{BgA}@YQc@O{@K{BGi@Ii@KsBm@g@Gg@Ag@Fi@L}AdA_@{@CGmBtCKQ{@oAQAMFqAdCANBLLNMOCMD[lAyBLGP@z@nAJPlBuCKQgEiGuB_DsAkBcE}EmFgGmDeEaAcA{BkCQY",
            "y}_iGnlgeNcAkB]i@_@e@g@a@_@Qg@QcAQcBU{@[wAaAmDwDkByB}@aA{CaE]m@M_@Og@Km@YgDWkDdASTINQrIsMNCOBcBdC}BaCtAsB?UGO_FqF_FiFME?QGOIK`@y@ASCCh@u@bCcDdB}Bx@iAh@s@z@gAn@aAx@oAfBqCf@l@dAv@^X\\Rx@ZbAXtAPnDRzBVN{CL}@BWSy@_@m@AMDOHa@A_@Nc@NWVUVMf@m@CSDQbBaCnB}CvDrE`CtCz@lApBmCp@_ARY\\g@bAmB`@}@d@s@bB_DlAsBXe@`Aw@hHkFh@g@z@eAvE|FpB{C`ArAnGlHb@^d@Xz@\\fB^v@\\z@p@f@l@z@lAbChDz@hANF`@~@\\fAThAV|BNjA\\lAXt@Zl@h@v@|@jAxExFn@~@`@|@Vr@^h@vBnD~@pAXc@rAmBJEFBRv@TfACDuBzAPp@Lx@@n@C`AIp@uBhOa@zCQ~@_@hAgBrDaAdBQZ|ApB`D`Ev@dAfArAgAsA[c@?XCV_@WKGe@m@C??Hd@j@?@ABMO]_@CC[GAb@[hB?tAEVO`@UX_@t@ELo@vDIb@K?y@?{BHAvBB~AExAGt@[jBe@zA]r@uFzJaAlB[d@qAzBm@v@_@e@gDcEiD{Dy@eAmBsBkBcBcDoCk@a@]OW?IrGAhBMMg@a@a@[OCs@D_Ed@QHiA|AWp@AJO@i@Oa@Ga@HYR]r@_@b@WLKJgAjBkAhBaBcCU_@_BwBsEoF}JkLaAcAkAwA{@eAEG",
            "y}_iGnlgeNcAkB]i@_@e@g@a@_@Qg@QcAQcBU{@[wAaAmDwDUUW`@cD~EgGjJwElHaAjBg@hAiGhPoBfFuHzRcApBm@bAa@f@mC`DeB|BuFdHcFrGuA|A{AlAmAn@qGvCaAh@cAp@_Az@k@l@mB|ByCdE{@fAaDhF]_@EFqCrEOTgB}BUWoAjCo@rAn@sAnAkCY]Y[d@_ATg@SUx@cBUYCGAMDYLa@aEuEsEmFqAqBcAsBgAcBWe@aAiAyBcCqBeCYc@g@{@_@_Ac@eBUiAUyBuB}WUgC[mBYeAm@cB_@w@cBiCwD_GmCeEoCoEt@mArDqFrDsFvAwBUYyAuBe@s@m@cAs@}Ag@sAiAkEs@cDY_Dk@mI^C@_@dAMb@KFO@IAM@LAHAJEBc@JeALA^_@BWoCMy@hEgBb@MbDYlBSf@AtAHz@Jt@NXF`IxB|ANnAD^Ft@h@TRr@bAR\\dBgCjBqCtMyRjCoEZ^lB_D|@}AHLb@^\\D`@EVQTWhDzD~J`LlBdCpA`BnE~ElCxC~D|E~DrEtGtHz@fAdF`G|EtFN]t@eAZ_@ZW\\Qd@M^CX?h@DnDb@t@@t@S^U\\]x@oAvBtCJF|AlBh@n@lDvDdAz@|@f@b@Nn@Lz@HpAXb@N`@Vh@j@X^lA|BDD",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAYN{AF[Pa@rBgE^a@\\Ib@@NDFDBMPy@l@gCJD\\wARk@d@}@xBzByB{Be@|@Sj@]vAOGc@Oy@I]@e@F_AXYNi@n@cAlBoCuDQEODmAlBIXKFKLAA_@e@~AgCbDyEfC}DzI}MbBiCjCaEnByC`IyL`@h@jBzBZFzBvCtAhBx@nAx@dBnD`Jt@xA{AbCMVCP?p@Kj@Wd@a@l@]r@k@~AcAiAIOHNbAhAnFzFl@r@bIrIv@z@ZgA~C_Kl@}BfA`ApE|DrAjA`@d@fIzKb@f@j@f@tAn@x@Pv@Jr@DlDGAvBB~AExAGt@[jBe@zA]r@_BnC{DjHw@rAqAzBm@v@_@e@mAwAsCiDiDcEmBsBa@a@aDoCwBcB]OW?MEcCOeAC_@Do@`@eB|A_@X]J_@@[OaA_A`A~@ZN^A\\K^YvBkB\\S^EdABbCNLDK|JQQ_Au@MGS@yEd@WFOLy@hA[n@EXYAs@Q[?e@POZ]h@_@`@UJKPmCjEyCoEaAoAqDiE]a@Te@Zu@Lc@Jo@Fq@@k@?Sy@@QAOGQOe@k@d@j@PNNFP@x@A?RAj@Gp@YrA[t@Ud@oEeFmDeEaAcA{BkCc@q@",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAYN{AF[Pa@p@qAJR`@\\n@\\t@f@j@r@LHFEN[`AyAXe@?GAOKOeBqBQMH_@r@yCDUJDPLxGbIhBlBZb@d@x@Rj@R|@VdBDb@iBh@SJc@d@u@lAODMAeBuBcBdDIPiAwA{@eAW_@",
            "k~_iGvkgeNV^z@dAjAvA`AbAcArBkEtIcEfIx@`A^`@_@a@KD[DWBQAMGEGEFm@|@}D~FeBhCiB}B[_@cC{CuA`CMZa@BkA`Cu@tAIDK?uAaBeBlDyBcCgC_DmCwCmBmB{@o@yAi@WQYS{@gAcDnGWd@Ve@bDoG_CiCz@kBH[N{@F{@CcBEi@Mu@Uw@kAgCgA_C_B_Cu@mAoAdBiAlAQJPKhAmAnAeBt@lA|@pArB{ChCkDXYp@_@nBa@EoBMaA@QHQj@w@XKC_@LOT]iBuBhBtBbDtDN]dAwAXYd@YNGj@Kv@AbAJ`CZl@DXCXI^Q^Y\\c@jAoBT[VSj@YfA[P|AQ}AgAZk@XWRUZa@r@vBtC|@`A~@hAnAtAhCpCf@^n@`@f@Rp@PhC\\fAb@f@`@^d@\\h@n@nA@B",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAYEAEj@CLABM@At@?JCBSLK\\Gp@Ox@M\\ODKAYQy@SgAK?hCKzAKn@G`@Wv@Sd@OZUJa@r@_CwCeHaI}DuEs@|AEBKQIN}@|Ar@z@JBDARUFOGNSTQAs@{@|@}AHOJP@?BEr@{AeAoAsDoEqBaCN]dAwAXYt@a@j@KZAv@DnDb@t@@t@S^U\\]bBqCZYZSh@UjBg@[}C?SDOT_@U^EN?RZ|CkBf@i@T[R[Xi@`AvBtCJF|AlBh@n@lDvDl@h@NJ`BaDzCaGPYVYVQx@[d@K`@Cf@@^Ff@Nm@hCQx@CLGEUE]AOD[N]h@sBhEGPKp@I~@|@Nf@P^Pf@`@^d@\\h@p@rA",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAYEAEj@CLABM@At@?JCBSLK\\Gp@Ox@M\\ODKAYQy@SgAK?hCKzASpAk@|AOZUJKRuAgBcCqCeDwDaBoB{@aAVe@BCPARc@HCNBHHp@|@rB}Cp@gALOGO?A?GAGDIBMEYNQz@yADS?UeAwAVc@wBuCh@aAZYZSh@Uv@SRhBSiBw@Ri@T[R[Xi@`AvBtCJF|AlBzAbBzBbCl@h@NJ`BaDzCaGh@s@VQx@[v@M\\Ax@Hf@NI^u@bDCLGEUE]AOD[N]h@u@`BeAxBKp@I~@|@Nf@P^Pf@`@^d@\\h@n@nA@B",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAY{@Io@Mc@O}@g@eA{@mDwDUYR[hDiFxDoFjCcEhEwGjEwGvA}Bt@wAnBmCf@y@zHgLp@~@jBzBZFzBvCtAhBx@nAbAaB\\o@]n@cA`Bx@dBnD`Jr@tAt@hAbBhBnGtFbCvBxAnA~CkKn@qBdC~AqC~ICN@L\\ZbA|@PCHOnC}IvB~A~BlBx@lAVh@Lf@Lr@Ft@?v@Ev@WtAP?THRFRBn@Kb@@XCx@_@N?x@d@\\Q@JBNFFlA`@K~@GfBJxQInAOjAa@dBa@fAgAnB}B`EmBnDcBzCq@`AY\\MQmAyAaAkAsCgDkEcFwBqBsD}Ck@a@]OW?MEyDU]B]JWNIFHGVO\\K\\CxDTLDIrGAhBMMg@a@a@[OCs@D_Ed@QHiA|AWp@AJO@i@Oa@Ga@HYR]r@_@b@WLKJgAjBkAhBaBcCU_@_BwBsEoF}JkLaAcAkAwA{@eAW_@",
            "k~_iGvkgeNV^z@dAjAvA`AbA|JjLrEnF~AvBT^`BbCjAiBb@s@b@w@JKVM^c@\\s@XS`@I`@Fh@NNA@KVq@hA}API~De@r@ENBhA|@LL@iBHsGMEyDU]B]JWNeCxB[N_@DYEUOTNXD^EZOdCyBVO\\K\\CxDTLDIrGAhBMMg@a@a@[OCs@D_Ed@QHiA|AWp@AJO@i@Oa@Ga@HYR]r@_@b@WLKJgAjBkAhBFJyDfG[r@}AlDLTn@dA~@rAzCpD{CqD_AsAo@eAMUm@nA}@pB}D~Hw@bBk@|AYz@e@A]IKMKUYYg@a@INOHQ@QEMSIWAYM?MHILCPFl@k@VWh@}@tA]v@m@U{AyBu@iACU?YU_CO@MGSWcEmFo@fAi@hAuDnFaAgBXc@?Qc@q@EI?En@gAg@u@tBaDk@cAQWUSw@e@Sa@Ki@SkB?QN_A`@}@`A_Bq@_A[e@Se@k@wBQu@b@c@|DcEh@k@`@u@j@oA^q@F_@N[j@}APmAJaA@]R?pBXTqBJ@NEL]VkBJ]RMROJON{@Ig@pAXb@N`@Vh@j@X^Vb@h@fA",
            "k~_iGvkgeNV^z@dAjAvA`AbA|JjLrEnF~AvB~BnDyDfG[r@iEnJ}D~Hw@bBWl@dCnCdBhBrAsCsArCXZg@jAuApDINKDKAOMWz@sAbB[f@m@bAmCvDe@|@IZERv@zAATELaBbBi@dAUx@Dl@?PNr@D\\?\\k@Qg@IyDCChBIfAOz@U|@]z@aCnE[d@UX]V{@d@V|A{@Ik@SQS_@w@_A_AUKs@Qs@Ki@?}CiBi@]s@q@i@[g@QMKk@m@i@GoAJc@J}@b@K?II}@aA_@y@k@aA_Au@WS[_@i@eAa@e@i@c@m@i@k@gAw@eAKOgAbBsApBY]i@o@U{@YPGMSa@a@g@OMWO_@QOOOYGMGKc@?MO}@dB{DxHg@x@s@|@u@n@k@^}DtBqC~A_@Xs@p@oAbBjAvAkAwAnAcBr@q@^Y}HiJc@q@oC}EyAqCWm@Q}@q@eEYcA_@u@[e@][aC}B{A_BsE}EmC{Cq@bAo@y@_DzEaBiC`BhC~C{En@x@p@cAzBgD~McSxFwIzD}FjEwGeAsA[[SK]IuBIk@EUKWScBoBlAeCL[H_@V}DF}@_AK_@AkIJoAAaAKw@Wu@[e@Yd@Xt@Zv@V`AJnA@jIK^@~@JhEn@`AX|@d@b@Z|@z@rA~AbAlAJO|HkL`EgGrD{FxAcB|BmDW]jDqFFUAUKUk@o@j@n@JRZo@z@fAdF`G|EtFN]t@eAZ_@ZW\\Qd@Mx@CrD`@~@JZATE^M^U\\]x@oAvBtCJF|AlBzAbBzBbCl@h@VP|@f@r@TzAPpAXb@N`@Vh@j@X^Vb@h@fA",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAY{@Io@Mc@O}@g@eA{@mDwDgKxOcErGi@t@wAfCaAvBkHrRO^SWk@g@UQcAc@y@]QMa@c@g@o@gBlD~@dAv@l@s@vB@RHNIOASr@wBw@m@_AeAfBmDcBiB[_@n@qANe@Nm@J{@@_@A{@C_AIk@Sy@Wq@sBoEs@kAmAkBe@s@yAqCQ[u@{@OU_BdCaEfGQSC?UH?IISaGiHWa@w@y@e@[OC?KGMoBqBm@a@k@SF_@Bq@@s@?C?BAr@Cp@G^j@Rl@`@nBpBFL?JNBd@Zv@x@V`@`GhHHR?HTIB?PR`EgGrD{FxAcBzIkNP]TVbGcJdAcB\\h@v@pAf@v@|AiCxAeCz@uApA}A|BiCiCmEs@s@USTRr@r@hClE}BhCpBhD^|@Pl@Lp@JtAt@hJNl@L\\Th@r@bAvBtCJF|AlBzAbBzBbCl@h@VP|@f@r@TzAPpAXb@N`@Vh@j@X^Vb@h@fA",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAYEAEj@CLABM@At@?JCBSLK\\Gp@Ox@M\\ODKAYQy@SgAK?hCKzAKn@G`@Wv@Uf@MXMBSZa@r@gApBSZiBpBkB}DjB|DdBmBSWz@kB@OCOeBoBLWBk@uA_BqFmGaCoCk@jAGPCBMQk@`A}@zA}@{@kBaDWc@UQYImACa@CWGWSKKe@r@c@d@b@e@d@s@EKIWKq@Cm@?KdBYf@U\\WtAiBN]t@eAZ_@ZW\\Qq@_Cw@kA|EmHFO@QIUsAsBrArB|@uAr@dAHDJAp@eAHINCv@Oj@tHHr@Nl@b@fAr@bAvBtCJF|AlBRTR[hDiFxDoFjCcE@AA@kCbEyDnFiDhFSZTXp@r@zBbCdAz@|@f@b@Nn@Lz@HpAXb@N`@Vh@j@X^`AjB",
            "k~_iGvkgeNV^z@dAhAvAHQz@eBZk@JS~ApBLDLAHEp@gA\\a@PMrBo@UkBQ_ASq@Sg@[g@l@{@RSh@i@JO?WGQw@{@uFqGcAmAwAuAe@|@Sj@]vAOGc@Oy@I]@e@F_AXYNi@n@cAlBoCuDQEODmAlBIXKFKLAA_@e@~AgCbDyEfC}DzI}MnFkInByC`IyL`@h@jBzBZFzBvCtAhBx@nAx@dBnD`Jr@tAt@hAbBhBhA`AqAjDpAkDxApAjB`BbCvBrJnIrAjA`@d@hFdHcA|AyAuB{AqBMEKBIJ_@r@^s@HKJCLDzApBxAtBbA}A|AtBb@f@j@f@f@Vl@Vf@LhANr@Dx@ArBEAvBB~AExAGt@[jBe@zA]r@uFzJaAlB[d@qAzBm@v@_@e@gDcEiD{DkC}CwBqBsD}Ck@a@]OW?IrGAhBMMg@a@a@[OCs@D_Ed@QHiA|AWp@AJO@i@Oa@Ga@HYR]r@_@b@WLKJvA~ADL?LTVLJH?TCTHAHH`@Xp@DnAEoAYq@Ia@@IUIUBI?c@c@E[wA_Bc@v@c@r@kAhBaBcCU_@eAyAkBwBaCuCwDpHvDqH}JkLaAcAkAwA{@eAW_@",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAY{@Io@Mc@O}@g@eA{@mDwDzDeG~CuEj@}@`L_Q~AcCjRuYlCdDZF`BvBfAxA`ArA^j@r@gA^o@rAmDVa@VWXOZGbDI\\@^Lb@X`@d@zDxE~A}CJS@UEOkC{CSYHOjAsBHQdBvBbHfInElFhHvIdL~MdGrHvBpCzFlH|DbF~IbKt@uAd@u@LOVKfBUZGROZa@lJgNb@w@Nc@R}@Fg@DeAAaAq@Ce@ISK]]Yo@E]Do@FYGXEn@D\\BLT`@\\\\RJd@Hp@B@`AEdAQjAY|@c@v@}EhHkD~ESN[FgBTWJs@dAqAzBKRNPFODAb@h@n@hAdB|Bh@f@b@PRGPO^[VGf@TSh@Yr@OXMVCj@FT@\\W`AC\\If@Sl@Gl@?p@An@EpAOzFFbBEZI\\IPQNi@^Y\\_@r@GZBRP^L\\Bb@?j@Ih@KTUl@?ZLfADb@L^Zl@DPFf@^|@`@vAh@jBHz@@VGd@Ep@?`BDRLRRj@@n@MdAAJIDUDY?[Eg@OUMO[BkAAi@QEc@GMI[[w@_AgD{DmCuCm@o@a@Yi@Co@b@yA`CsAhBOPs@b@aC~AODQDcAL?XAt@Gh@eAdEGz@?f@aALk@NULk@`@s@`@k@Pg@R_@d@M^Cb@Dj@Dl@Sd@m@Za@JULQNM@]@UJSTy@Ja@N]TYX]n@Oj@Mn@Gr@GHCTMh@a@`Ac@z@iAmAeFcF}HeIqEcFQQPYdBkC[]sA{AFQDYKe@Ss@s@oC?{@BiC_AE{@G}@KiAWw@[i@Wb@sBFc@Da@B}@?q@YwGEm@c@wB_@{@OY}HiLsAkBcE}EmFgGmDeEaAcA{BkCW_@KQ",
            "k~_iGvkgeNV^z@dAjAvA`AbAcArBkEtIcEfI_BdC}D~FqDrF}HlL~CjFf@`Aj@`BkClBI_@g@eBIOMWkAeBxA|BHNf@dBH^jCmBk@aBg@aA_DkFs@}@eHgIyBcCyBqC{@aAkCmCi@m@k@g@UQcAc@y@]QMa@c@kCyC[_@wF`Li@x@[Z_@Rc@PqAXoFhAi@Ra@Xm@p@eEfIm@lAi@x@sAxAcA~@c@TuB^MoBIe@OUOMNLNTHd@LnBtB_@b@UbA_Ag@iAsDiE{AcAMMAE@[ZkBL_@p@eANUFY@USgGIWOMUEIWMQIO|@yAlC}Do@w@e@k@SQ[KYEiCIUIWOoByBtAuCTcAp@gMJkBJc@Pc@`AeB_@e@qByB?IDk@AEkAsAKGaASU??T]nBIRgAhBfAiBHS\\oB?UT?`ARJFjArA@DEt@pBxBjAxAVVVLxBr@VBPELQ|@cC?KNBd@Zv@x@V`@bAgD^{@`@u@|@qAr@m@|CsBx@w@^w@Tq@Jm@H{@Bs@?gAO{BAk@@m@Fi@Li@Z{@~C_HPi@`@qCdK~Cb@Dd@Cf@KZMJGZbBPf@l@r@tBaD^QTUb@XFSf@aCJc@FDlAr@\\\\NPxDzG~@`BZ|@Jb@L~@v@hKJl@Nf@L^\\l@zC`E|@`AjBxBlDvDvA`Az@ZbBTbAPf@P^Pf@`@^d@\\h@p@rA",
            "k~_iGvkgeNV^z@dAjAvA`AbA|JjLrEnF~AvB~BnDjEnGR`@N`@p@e@XQd@Uf@Ih@Cl@D\\FvBn@l@Jr@DxADj@Fb@Lx@b@vExD|Ap@ZLLu@f@cBNc@h@eAb@m@|@_AnAeAtCyBf@e@l@w@x@uAy@tAm@v@g@d@uCxBoAdA}@~@c@l@i@dAOb@g@bBo@rDkCbO]vAo@zAYf@[d@GVe@x@[b@oEvGSZ^b@zBdC~BdClHrHjFlFZZ~@qBFWGV_ApB[[kFmFmHsH{FkG_@c@U\\wApBoBpCQHOX_HbKyAxB{EdHcD|E]k@Y]s@s@y@i@UbAYx@eAtAiC~DcAzAQJI?OIwAgAs@e@iDkAc@Ue@g@sAaBuBgC`@y@\\{@T}@N{@HgABiBCy@McEk@D]@u@Ek@Qg@_@oC{CW_@{@_C]s@aF}FoEyGoCiEDIE]Ky@Ms@Yk@c@a@u@Si@CAO_@y@[{@o@w@sAq@W~@c@nAKVJWb@oAV_AhAaEbA_CxEkJtDuH\\g@bAcADSnCuCjBqB`@k@lBoDTKN[j@}APmAJaA@]R?pBXJ{@Hu@J@NEL]Ny@Fq@J]RMROJON{@Ig@pAXb@N`@Vh@j@X^`AjB",
            "k~_iGvkgeNV^z@dAjAvA`AbAcArBsCxFhGlHwCjGGNFOn@{AfBoDiGmH{EnJqAzBkF|HqDrF{D~F_AtAaAvAIMY]QS}KiMgC_D{AcBsBsBuDnJ{@zBz@{BtDoJk@m@e@a@oBw@q@e@{DqEoH_JeArB?B?CdAsB{BkCh@{@TWvCqClA_BtEwGt@}@ZW`@Ss@gCr@fC^KtDo@f@U\\WtAiBN]t@eAZ_@ZW\\Qd@Mx@CrD`@~@JZATE^M^U\\]x@oAvBtC|@`AjBxBlDvDvA`Az@ZbBTbAPf@P^Pf@`@^d@\\h@p@rA",
            "k~_iGvkgeNaAkBY_@i@k@a@Wc@OqAY{@Io@Mc@O}@g@eA{@mDwDgC}CKGwBuCs@cAc@gAOm@Is@w@kK_@_B_@}@qBiD_CaEQSkAw@]So@i@oAyAiE_FYW_Ai@o@WkDg@u@[c@]_DgDYn@G?QCc@GKGUd@c@c@aApB[f@]`@c@Xc@Ro@LkBRi@Ns@kEc@Xb@YY}AE]?w@Fq@rCuM~@iEkCcAwAk@k@UiBw@_Bw@c@[i@o@QWU]jDeFlAgBrD{FjFeI`@m@vAsB`DyE|AcCz@iAxGcJjBwCXg@XGRET?ZB\\d@^f@b@j@jB~B|@aB\\q@NWh@o@v@[nD[^?`@H\\Ph@d@VUb@w@lA}BNWR_@r@|@dBxBvAdBhErFh@cAt@wAu@vAi@bA~D~EdCzCbMbOzD|EbCpCjJ|KnBvB|B`CnDdErDzEf@p@`@z@vChDhArARCRXnAvADKHOBGD@NAb@o@Pg@n@eAL?vAlBVa@jAoBtC}EzDlF^^d@XLBMCe@Y_@_@{DmFq@fAoDdGiBzCw@pAkBwBuAaBMOSBsDmEm@o@]OGGS`@EBKCgAuAOESGGBONOBW]_GtIuGnJqIpMaD~EsKlPmGrJcDxE_BfCyA|BQVTTb@d@hCpCvA`Az@ZbBTbAPf@P^Pf@`@^d@\\h@p@rA",
            "c~_iGblgeNjAxAjAvA`AbA|JjLrEnF~AvB~BnDjEnGR`@N`@p@e@XQd@Uf@Ih@Cl@D\\FvBn@l@Jr@DxADj@Fb@Lx@b@vExD|Ap@ZLLu@f@cBNc@h@eAb@m@|@_AnAeAtCyBf@e@l@w@pA{BZe@`AmBtF{J\\s@d@{AZkBFu@DyAC_B@wBIeHD_CFw@RwAf@kBn@wA`BaDlAyBhBuDPe@g@k@Nm@ZwAJw@p@gFXsBYrBq@fFKv@[vAOl@f@j@Qd@iBtDmAxBvBpCzFlH|DbF~IbKrGrHfKtLfEvEfBlAz@\\VFEZG\\El@ATBPHNDHBd@Jl@Jf@LXrA|A_BvDj@x@v@bAPJPCQBQKw@cAk@y@uA{Au@w@MGWC[DQJKPKToAnDyArDaAvBKZUlAAVNzCFPLPhBxBd@Rf@VdCnCg@fAu@nAiFbIiKxOuEpHkDjFQPgCtDyCtEGTyB|CoAlBqAnBeCxD{BmC_AmAwLeQaC_D{E{FaAeAkFmFmHsH{FkG_@c@U\\wApBoBpCQHOXz@`Ab@^r@f@dAx@\\^nBtCfBhCbAyBRc@HW?SGSmAkAlAjAFR?R]z@cAxBgBiCoBuCo@o@oA}@{@s@{@aAXq@fBsCf@w@H_@@G?WW}@uCeDa@a@L[p@{AaDsD{@sACIKo@c@e@\\}@~@kELeAB}@?q@_@eIc@wB_@{@wEcHyCoEaAoA_LqMmDeEaAcA{BkC[e@",
            "c~_iGblgeNjAxAjAvA`AbA|JjLrEnF~AvB~BnDjEnGR`@N`@p@e@XQd@Uf@Ih@Cl@D\\FvBn@l@Jr@DxADj@Fb@Lx@b@vExD|Ap@xAl@dBv@uAfIyAtICJ?T@Fg@Va@HgAb@UZWvAENwBISIKNeBbCsDvFoC~Dg@l@QHkEvGmCzDoLfQk@{@q@u@[WwEkCoHiE}A{@i@Og@IyDCCy@McEk@D]@u@Ek@Qg@_@oC{CW_@{@_C]s@Y]_BiBcBfD{BkCMXLYzBjCtCuFT]dAaA~AqAVWLOIQcAyCiB}Fe@gA_@s@s@oAPUFQAOc@q@CI@Kl@_Ag@u@fF{HpDsFjF}HpA{BzEoJvEmJaAcAkAwA{@eAOS",
            "c~_iGblgeNiAwBY_@i@k@a@Wc@OqAY{@Io@Mc@O}@g@eA{@mDwDgC}CKGwBuCy@nA]\\_@Tu@Ru@AoDc@i@EY?_@Be@L]P[V[^u@dAO\\uAhB]Vg@TuDn@aA^[Vu@|@uEvGmA~AwCpCUVgCzD{ClEiA`BcC|E|@r@a@hA?FUv@G@a@_@{CaBiEtL[EZDhEuLy@uAmA[@KGWgC{Ce@k@SQ[KYEiCIUIWOoByBtAuCJ[Hg@VuE@M_AKgAAqILaAGc@GkBs@w@k@sA}AmD_EiDaEvDiILm@H{AFqCr@_DsAyAo@gBWk@c@o@cAiAgAuA}@{As@y@e@a@b@iALYZe@R[SZ[d@MXc@hAd@`@TRr@bAR\\dBgCjBqCtMyRvI{NvG{KzKoPfD_FlDcFPVdBmCvBeDT]tAqBnC{DnB}Cr@eAb@k@BTDpAdBtDzAdCJRUXiApB]x@WlA@jBB|@Nv@Rn@T`@fF~Fx@~@fAzAlCdDjBtBbLnLtNdOr@r@XLfJ~JjDbDTRFRtApAFJRNr@Vt@Jt@Hd@HNJP`@wA|BwG~JiGrJmG`JiBrCj@n@lDvDvA`Az@ZbBTbAPf@P^Pf@`@^d@\\h@x@~A",
            "c~_iGblgeNiAwBY_@i@k@a@Wc@OqAYEAEj@CLABM@At@?JCBSLK\\Gp@Ox@M\\ODKAYQy@SgAK?hCKzAKn@G`@Wv@Uf@MXMBSZa@r@gApBSZiBpBaCdCw@yCc@}@cAqAq@vAJLSUiAqAmAuAGCK?MLeAzBENS?e@hAE?GE`BoE\\w@b@_AvAgCh@u@|C{ET_@UYfAaBrB}Cp@gALOGO?A?GAGDIBMEYIBIEwCkDiAcBK@[@_AK]E\\Dd@Ft@@t@S^U\\]x@oAvBtCJF|AlBh@n@lDvDdAz@|@f@b@Nn@Lz@HpAXb@N`@Vh@j@X^hAvB",
            "c~_iGblgeNjAxAhAvAHQz@eBZk@JS~ApBLDLAHEp@gA\\a@PMrBo@UkBG]j@ULGhAgAJ?HBHJDZb@`El@tFANELMHgCl@Ht@Bd@?j@Aj@Gp@YrA[t@Ud@\\`@pDhEPRrAjBtB~CbBmCt@oATK^a@l@eAd@QZ?r@PX@DYZo@x@iANMVGxEe@RALFpAfAJ}JMEcCOeAC_@Do@`@eB|A_@X]J_@@^A\\K^YdB}An@a@^EdABbCNLDK|JqAgAMGS@yEd@WFOLy@hA[n@EXYAs@Q[?e@Pm@dA_@`@UJu@nAcBlCuB_DsAkBQSmAuAeBlD|C|DDPATKTEHDIJU@UEQ}C}DdBmDcBsBmFgGmDeEo@pAmAbCyBjEwArCvAsCfEoIn@qAaAcA{BkC[e@",
            "y}_iGplgeNlCbD`AbAlDdElFfGbE|ErAjB|HhLNX^z@b@vBDl@XvG?p@C|@E`@Gb@_AjE]|@Uh@oA~BwAdCqAhCU\\o@v@J\\Pp@Jr@NdEFvAbAI`@E^MVQn@w@LIL@XZY[MAMHo@v@WP_@La@DcAHJzBPjCPhAVt@j@fA`AzAcD|EsLjQsHfLkBvCeA{ASGo@Co@GOK}@wAKKKEG?EIe@w@KCM@q@fAS]GEI?a@La@j@kDfFgBmCWYsAuAWSe@Qi@Kc@Ak@Dc@PYVc@p@WVUNyBz@UNaCnDkBpCaBmBMOECDBLN`BlBjBqC`CoDTObAa@GYGq@CcBl@qJJ_ALk@Xu@HQ{A}@i@]s@q@i@[g@QMKk@m@i@GoAJc@J}@b@K?II}@aA_@y@k@aA_Au@WS[_@i@eAa@e@i@c@m@i@k@gAw@eAKOgAbBwB`DiDfFoH_JmHwIkAkAg@o@Qc@K_@I]QH_@R{EvBaCrAgAx@m@n@gCxC[o@}@aBWm@Q}@g@aDc@gB_@u@[e@][KI]z@Ub@eA|AgAdBc@z@b@{@fAeBdA}ATc@\\{@uBsB{A_BsE}EmC{CzBgD~McSxFwIzD}FjEwGeAsA[[SKOENkBBQDQfA{DLSh@q@K]Lw@MIf@gAVc@RWJGKFk@z@g@fALHt@p@tDhE~BjCfD~D|BeDzFsIrCkC`@g@lDcFhCkDXYp@_@nBa@dBYRIb@Wn@u@r@_AN]dAwAXYd@YNGj@Kv@AbAJ`CZl@DXCXI^Q^Y\\c@h@{@vBtC|@`A~@hAnAtAhCpCf@^n@`@f@Rp@PhC\\fAb@f@`@^d@\\h@n@nAR\\",
            "muihGtrqjNlA|EZlACTCf@GBTnA?NT`@@XERH^Pp@Cb@H`@l@lCz@VO`ABJLCZQN?VPLRLCn@n@@RTnBFRLEXvA\\`BD`@Z~AXlARj@f@OPKQJg@NSk@YmA[_BEa@]aBYwAMDGSUoBASo@o@MBMSWQO?[PMBCKa@MyA_@ACAE?OE]cBjAGOwAbAWRGJa@NqAv@UNw@VMJEOyBlAI[SL}BtAiB~@K_@a@Yc@OOA?JEJYR{@h@YLEAk@{Bp@aAm@{BZ[DDH@f@W`@[FEG[`A}@p@y@|AqBRM}@mDNCNB\\OZWt@q@xAq@~A_AfBmAJ\\tCiCtA_B",
            "muihGtrqjNlAiBbAcBlBuDd@o@RMTEd@BNFv@b@|AbAZZfB`Cl@f@bDpA|EjBj@ZHJTd@Jj@DTEn@Ip@Mf@Oh@IRHFLBL?RIPB^DLz@P^ZJFR?L_@VEPFn@@BLFs@rB?\\FLZ`@ADIl@XV`BtBtAyB\\gA@MHQBECDIPAL]fAuAxB|@~BP|@DvAI~@Oz@a@pASfAg@`EMdAGVU`@UVlBjHLXYZs@`Ac@gAUs@e@uAO[GEM@_@J{@h@[NI?GESa@Ei@GMEQuAx@BHcDvBSc@UMg@@aAPQ_EMIGKKn@W`AMl@CVSZMTIZ@xAIv@y@d@S}Ae@_EGk@U}@QFUKI?QXCV]RQCc@Gs@MOMG[GMKAOe@EBCKKm@cAh@O?{At@UNq@iCcBgGeAwDqBeIaAgEcByG{@sDPGNCNB\\OZWt@q@xAq@~A_AfBmAJ\\tCiCtA_B",
            "muihGtrqjNuA~AuChCoBtAkBhAS\\IROt@Cx@B`@J`@hAnEv@zCwBjAJb@QJeCdBaBz@qDxBw@h@FXDRGBEHCD?JKPD`@~@tDw@b@cCvAaA{D?Kz@i@Kc@EQsAx@gEfCSNSV[h@?I?WEUISMMQIOA_@q@Wq@eAyDgCaKM[QQUEqHnAgAHaAEu@Mq@M[AUDSHcAh@SDk@Vo@ZqATkABs@Gu@Qc@Qu@g@i@[e@WM?i@_@Z}ADg@Ca@oAaEMc@OQSQEKMa@Me@Z[tAeAhDsCAcBAm@XCfAG|@WdAa@r@c@xBiBlKeJ|@q@hBgAhIqFcDgLbDfL~HgFnBmA\\|An@lC~ArFv@pDd@hBJb@\\pAb@jBn@Y|CiB\\pAl@a@Lj@NT^SVE`@~AJ@lAu@bCoBZ?j@lBV~@TOHDFLNEN?LGHGLCTMRn@Ld@BLNQ",
            "muihGtrqjNuA~AuChCoBtAkBhAS\\IROt@Cx@B`@J`@hAnEv@zCwBjAJb@QJeCdBaBz@qDxB_Aj@OBGEM@E@MTMZMHeDjBaCrAuAbAUDc@?SKKCG?Q[Q]Si@eAyDgCaKM[QQUEqHnAgAHaAEgB[[AUDSHcAh@SDk@VWLWLw@NYDs@BW?s@Gu@Qc@Q_BcAe@WM?i@_@c@Y_@Q{@YsAWq@OSOKCoASPuCAo@Kg@Jf@@JbBiAxBkBZ[VQEIIGiA_@]QIIYo@S}@Iq@?UF}@Po@[Sg@c@[c@We@{@uBm@_CHGj@o@^m@j@wAt@uBD_@A_Aq@sC~@c@b@SE}@Hi@?k@QaAWm@_@w@Ak@D_@DSd@y@Rg@OaBbAmA`B_B`J}GnB}A|@}@l@_A^s@DUXPHHZiAPc@LMdDoBr@_@d@OIk@D?`EcAd@GZBPPx@|Cj@xBzCgBJb@j@]RMTCFFR`@^jAL@h@Y^MRAVHbAh@dAl@VV`AdD\\WD\\ALKHYd@RpACpAEJTx@{@j@uAt@oBjAjBhHbAnDz@xDpBdHfBzH\\tATz@Nn@l@UDGjC}ALGNh@Lf@d@[FELj@NT^SVE`@~AJ@l@_@|@o@dBuAZ?v@lCJ^TOLNBBNEXCLKVGJIJ^L`@J`@NQ",
            "muihGtrqjNlAiBIY^i@vAsCdB}DN?HYJk@X_@jEgJvA{CRSa@w@qAoCp@aAq@`ApAnC`@v@jBaELKXOHEVg@N\\HtAPb@rBrDxAjAAd@?THZPHFz@Rv@^h@\\b@b@Xz@LbAVd@Tt@XNNHBN?JFB?FEHTR\\XX^V_@WYYS]IUGDC?KGO?ICOOu@Ye@UcAW{@Mc@YEC]Ts@f@IXCF_@ZTj@u@YmCgAIb@WOi@Qy@_@OBWhAgAe@G\\iCeAWrAQp@O@YICHI`@WKeAg@EXIXO?eB|DwArC_@h@HXmAhB",
            "ihihGbirjNaBt@SNSd@GRCL_@M[IQw@i@X]PcBjAGOwAbAWRGJa@NqAv@UNw@VMJEOyBlAI[SL}BtAiB~@K_@a@Yc@OOAUaAe@oAM?QRY`@Sw@YcAUXW\\[^mAuEKc@Z[x@w@RKFBb@`B\\Si@yBCWIKG[zH}CQw@Me@?WoAiFmBiHg@uBs@oC_AgE_DtB|J{GdL{HrI}FpAhFVrAx@nCTQNAHTP|@X[HKrAyAP~@@\\EZOXA@@ANYD[A]Q_AsAxAIJYZBRXj@h@x@bA|@Kh@`@VVLEXIXNHLl@FdAHZx@`CLJH`@Ll@@V?\\?\\Lj@JNi@nBChCEZBJBXANHRJb@?`@Er@Uh@M~@EbABXHb@",
            "ihihGbirjNzBu@tA[d@GRPd@Lv@\\b@b@EVAh@t@RVDPJb@X`@^PJLRRRx@r@DP?b@Nf@~@mBHGJAd@H\\w@L]XV`BtBtAyB\\Zf@d@z@jA`@rAbCu@v@QD@HCBTFbAa@fJGpCAl@C\\{@tCIj@CjEEl@HL@FPPr@r@\\t@KH_@`@OXWfA@b@Lp@`AdCHTCPq@zARVXx@x@tCm@uBkCjBFPBLDVATEL{AjBOHOBQCI`Bi@hB{AcA]eAORMp@M\\Y`@SPc@x@_@bAOZCRD`AGhBAxBmAB_EXi@BDjBBf@Cg@EkBqBNcAJg@NcAn@m@^_CrAkBnAgB~@{D|Bs@o@}@[a@OOKSW]w@g@`@sAt@uAd@]Fu@LaABy@Ay@I{AIg@Bg@@FKFS?WK_@GIEAHwBC_@B^IvBD@FHDHBJJJTL\\B`@@~ALjADxAIZEr@O|@a@j@Uf@]j@e@nAyAZg@`EeH|EoIMg@aIyZ_Bv@_@Jk@@SIMIkAg@i@Oe@Ko@E_@F{@^]TUXi@j@w@~@[j@Of@Ml@A\\e@_@GSAMYYOMU[Kk@Q_@[Sa@~BU|@a@dAOV]`@i@f@e@TyAqFOe@KM@KEMKCC@o@cC}ByImAyEGq@Ak@@a@BM\\q@RWzEwCvBuAt@a@R?B?J?LIDO@OCQEGIGCAQM[m@]uA\\tAZl@^?H?HLFXpBiAdFsC|BuARMHZxBmADNLKv@WTOpAw@`@OJHl@Mj@YHIJYBQn@g@`Ak@VM`@UPv@z@VLi@Xc@ZUpAi@",
            "ihihGbirjNKs@?c@HmAPq@JS@QBcAUw@YOOA[HU]a@[MCM?[HyBpAU}@iBkHlAiBIY^i@vAsCdB}DN?HYDYdAf@bDtAnDxAbDrApElBGZShATHRH@FQfAj@ZHJTd@Jj@DTEn@Ip@Mf@Y|@y@rAiAfAg@Ta@Ng@HcF^{Bb@{Bt@",
            "ihihGbirjNaBt@SNSd@GRCL_@M[IQw@i@X]PcBjAGOwAbAWRGJa@NqAv@UNw@VMJEOyBlAI[SL}BtAiB~@FTkCzAcB~@KHCFCTIH@HBV~@tDdAvDzBvIFZg@`@YZa@`Aa@pBy@|Ec@|AYn@SXk@n@e@XQHEOsAaFOe@KM@KAGGIG?C@{BwIgAcEwAz@YiAOK[Gw@WSI_@M_@GME?IM[Yk@Y^U`@Qd@Kd@Il@QlCOnCKz@_@bBQl@S`@Y\\QLe@Ry@PC[W{AyEsQSq@eAv@sAjAgAtA}@dBc@fAgBdGmBpGc@rAaAlBo@v@_Az@kAv@e@Tu@V]HeALs@@aAEy@IYGs@Sk@Wo@a@g@a@m@o@m@}@m@v@aB`AOCEISJc@d@e@h@Os@s@sCoBoHoAcFaFgSkAyEoDpBmAp@u@d@WBw@j@_DnBSBKH}EfDOa@w@oCyA{FIo@DMFE|@i@]yAm@eCa@oBCU?QG?ECGI?SEQQk@}@gDqA|@MNEJo@\\mCxAkBkH}AkGdHyD~Q{JRCPBNJd@hAPj@Tx@`@bCLf@HJLBD?DGLODS@KK_@_@wAq@wCcAoDm@eC}@eDkBkHiBkGsBcIiBgHzBeCxDqEdEeF`@rAJHvAhFwAiFA[[oAfBsA`B}@dAa@rAYpPyB`L{AbEa@lFo@dJsAfGaA[mGHbBNrCtAUrCa@nCa@fBMZFnEo@`Fo@d@IdEg@zBUtPuBbGy@bAMH^~@tDxArFbBtG~@jDbA|DJ^s@b@r@c@ZnAn@dCv@fC^dBvA|EpAhFVrAtB~Gd@~@V\\Yl@f@bBRr@NR@\\JPd@Gd@@JFDJTz@f@tALJRbADV?X?l@Pr@FF]lAK`@?hAC~@EZDV?\\Tv@CbAAPKRQp@IlA?b@Jr@",
            "ihihGbirjNaBt@SNSd@GRCL_@M[IQw@i@X]PcBjAGOwAbAWRGJa@NqAv@UNw@VMJEOyBlAI[SL}BtAeFrC{BpAGH?G?CAEAGEGIGMAMHIVOXuAt@uBlAeB~@u@j@_@VUDc@?IGUGG?Q[Yk@uBwHiBkHOYSKKA{B^aFv@}@@mAQi@K_@EU@UHmAl@]Hs@^MDi@Pi@Jc@DmAAs@Mq@SsAy@aAi@M?UOi@a@g@W_@MmAYg@KFs@GUISRDSEMa@k@oBCGBCf@[H[r@m@HWHUVWZ[VQEIIGiA_@]QIIYo@S}@Iq@?UF}@Po@[Sg@c@[c@We@{@uBm@_Cq@eBkBsEWe@USq@g@k@SLq@j@mBm@u@i@mAo@sAKe@HW?W?VIVJd@n@rAh@lAl@t@dAqBlC_EzGkJ`C_C`J}GnB}A|@}@l@_A^s@f@uBJy@LgB?_BA]Mc@OuBmC\\C[OaCGuAXC|@INANf@@v@@ZA[Aw@Og@O@}@HYBFtAN`CnCa@fBMZFnEo@`Fo@d@IdEg@zBUtPuBbGy@bAMH^~@tDp@`CzCiBJGBNLHLXr@vCb@WLf@Np@\\ST~@MFh@zBCBDTZCRKNn@\\xAr@Nd@|AtAp@JHJOpBjEpB`Ev@rAP^dGdMR\\P^jBaERQDAE@SPkB`ESRiB|DsD|H[^YlANHLl@LpATz@f@tALJRbADV?fAPr@FF]lAK`@?hAC~@EZDV?\\Tv@CbAAPKRQp@IlA?b@Jr@",
            "u|liGjqocNn@?`D}@l@O`AIv@BPAjAYDBDPN~@~@SdAUFCxBg@ZbAHPF?xAi@bA]PGCYz@[RGxAi@\\KFABTVpB^|C`@rCfB~MjGre@e@NCFA^h@vEqCt@q@RBVBVdAvIpBvP~BjRcCt@cA`@}Cx@oF|AiD|@kBh@iBj@LjAMD]H\\IMkAUFkCx@uF`BmDdAyNjEkQnFsCx@qEtA}Bp@e@N[gCo@qFYeCw@V{DfAcBh@UqBq@aG}@iHkAkJy@sGgAiIMa@W_@e@_AcD~@iF~AcJlCaDbAKDM}@s@qFq@qFkAeJY_CQqACWDCpAa@zCaAvC{@h@QRDb@OLQZK^OCMZKfA_@dA_@LKHWF?Iw@[mCB_@BG\\K`@KJSRgB@K`@q@?AAA?G@CDGH@`@m@AGAIHC`@w@?CWkBWcBY{A{@eHGuCAMLEhBk@lBm@LE^jCPrAXpBbHyBnDiAxAKfAUpDiAhEwApKcDhFaB~@Ul@Od@Y\\Kp@Qh@MhA[CSDAjBk@~C{@nBk@~@]FNFGFKJ]XgAJMLIDXB@l@Md@xDNnAaAHm@NaD|@o@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFCDEWe@wDg@wDgAoI{@wGgBeNaBcMODQK]Ya@e@eBeBo@k@I?g@\\OJUIKy@CS[MKUMk@KMSOaCt@gHzBkDhAcEpA{C~@kA\\@Ne@|Ad@}AAOCQLE|Ae@zBs@jEsAtE{AfH{B`JqCpCk@hCg@hFaBm@mEGc@`Bg@vAe@`DeArAc@LESuAu@kFS{ARzAt@jFVhBjDkAJr@b@`Dz@fGz@|FdArHLv@HXtC|HrD|JlB`GDTFCpBq@`EqApAa@xCaAv@[VQ@G|@e@b@Yc@X}@d@AFGFi@V{E|AaJvCiA^UAuBl@y@TqNnEeDdAXrBBTOB{AXgAPaB\\YHZlC\\lCTvBd@xDNnAaAHm@NaD|@o@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFCD\\jCODD\\mA\\IRT|ALdAm@R}Af@i@PA|AyBt@xBu@@MAuB?cBBSDO@MIs@Ge@DACS`Be@~@]FNFGFKDINs@Ng@BGTOFZF?f@MPxAR~ANnAaAHm@NaD|@eE@tCA",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFOf@Or@KRIHGOKFuCz@BRE@PxACTG`@?`FyBt@U^Yj@?DBVKHINDNf@hAl@vADT}@XoCz@_Ct@\\fCBZ?Tw@VuBr@iFdBeBj@i@PUeBWqB_@OMSGYAq@Cg@?MDQHIFCKiAWiBe@qDY{BkCx@_Cv@uDlAEg@YuBkAqJAYJCbCw@bCw@tBy@|@W[eCSaBR`BZdCfDiAfDgAxE{AvE{AxE{ADVDAbA]xEyAt@WR|AlAnJDVBEBGTOFZF?f@MPxAR~ANnAaAHm@NaD|@o@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BUqBo@mF[mCXI`B]bDk@NCCUYsBEWPGUsA{@}GaAsHq@cFJEHGL[`@q@h@q@FEFCFWV}@X{@BQcAiHiDiV{A_LQiACSPG`DgArA_@~Bu@RGCQZSHCSuA]}BPEDE?IEa@jBm@AMEY@e@DoEDyD?Q?PAh@^FVFDLXrANLTn@d@hBTxA|@]|@Qh@@\\DH@nDpAZRLPHNLOLQ@BRp@LVRv@xBbJt@rCnA~ElA~ETl@Pn@FVV|A\\zAvEpR~BfJ\\xAzAtFtAnFt@c@`Ak@H}@BK@WDw@H{@L_@~BsAvCiAb@SbEgAbFi@zGSzGJzJn@jI~ARHSIkI_B{Jo@{GK{GRcFh@cEfAc@RwChA_CrAM^Iz@Ev@AVELUrBIb@E?MFm@\\GDv@~C|@pDoAeFOc@SQUMQ{@aDlBw@\\oDhAo@Te@LS@m@NoGnBkCx@[PKJBPiE|Ag@PoCz@sJxCiCx@uBn@_Bh@BVBBLA`@KHl@XbCPvAaAHYHuDbAo@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFCDEWe@wDg@wDgAoI{@wGgBeNq@yEkA\\mGfBeI~BmBh@[oCMeAlBm@lFaB`Bi@?EDCBKtBq@fDgAPJBR~@[fH_CxMkEnEuAdA[BAEUA]?OB]Pe@NWLI@LAMMHOVQd@C\\@l@DTC@t@nF\\xBvChItCzH|ApEj@jBNj@jBo@`EqAlCrG~@|B[LE@Db@@PHJNEODIKAQEc@]LAQS?_@FgJrC{Af@WTBP}Bz@gBl@yBr@{ExAuH`CcF~AFZF?f@MPxAR~ANnAaAHm@NaD|@o@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFCD\\fC\\lCLz@h@`EfAfIjB|NvC~T@HODqA`@cD`AuJzC{KfDoLpDyGtBu@VMBAGMkAa@_D[cCcBf@cDbAoAb@g@J[uBEOKOQIUBcAJk@R_@Zm@|@IF]aBOm@Wo@Yg@Ki@Eg@KkAo@eFo@mE_AoHoBaOm@mEKq@zDiAnBk@nAa@hCy@f@SD\\hAxI@VVC`Co@tDmA~Bw@FA@RrC}@nFaBhFaB~@Ul@Od@Y\\Kp@Qh@MhA[CSDAjBk@~C{@nBk@~@]FNFGFKJ]XgAJMLIDXB@l@Md@xDNnAaAHm@NaD|@o@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFCDEWe@wDg@wDS}Au@VyExAuFfBmCz@[{B[wBCQG@SsA?_@QF?GAMAEPEMgA~DoArEwAvBo@Gg@Ec@HUDQ@KAIVI?OBQFSN]Gg@l@Sv@WnBm@tEwANGT~Af@~Dr@rFf@hD`B`MBPXIhBm@|EyAZK[JwBp@oEtAkHzB_Bh@BVBBB@HC`@KHl@XbCPvAaAHYHuDbAo@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFCD\\jCODD\\`@xCd@xDDV_AXyBt@oBr@\\nCk@eFAWsDjAYJMu@Lt@XKrDkA@}A?sCHg@QyADACStC{@JGFNLMBILe@Pu@DKNMFCBVBBB@HC`@KHl@XbCPvAaAHYHuDbAo@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFCDEWe@wDg@wDgAoIo@eFSHyH~ByC~@kM|DOgACAi@LODOcAGo@Gg@XInBk@xDmAvEwAvBq@CSSaBe@sD]iCg@cE[aCs@oFUF]L\\Mb@MAGLEj@QDEBUFIZUXQF@`B|A~AdBb@XBRNGrAe@nC{@tFgBjIoCxAe@WuBbBm@JDBHTbBNEn@tEdAWbEoA|DoA|@Wn@Uh@QKBPEp@lFP|A^nDr@bF`@zBb@nBVt@V^TRn@\\~@Va@J}@X}@ZTh@i@PmGrBa@Nb@nILt@D^YNe@NJt@R~A_Ab@|@lI}@mIcBx@u@`@OlA]nCK~@SLyAj@ADTjCaDfAeA\\ECWy@IWaAT_ATeAT_ARO_AEQCCe@Jg@LQ@w@CaAHm@NaD|@o@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BUqBo@mF[mCXI`B]bDk@NCCUYsBEWPGUsA{@}GaAsHCQpAc@bA[|CaAdHyB`DeA^`AbD|IzAxENj@|DsAjGoBlBm@p@[HGDK`CwANICKPKYiALGLd@`CuAaCtAMe@MFpAzEpAfF^pApCbLMH@Ht@jDHL@CFVL`@BTD`@t@QDZHx@Hh@Hf@DB@HHCBR^M@LRGHn@BNFJZbCxA_@pAa@@~@NADfAx@Ky@JFdAM@?\\IdAIJaAVK@MEM@KPOJgBh@OD\\dDd@bENzAQDKBw@XeA\\o@RaA\\kAb@e@^a@b@iAuAKGC?GNMOKRILy@tASRMB}@V}Af@cBf@UHCFCJqA^YLE[iDdA{GrBgA^eCt@{Ad@SHOFOgAu@{FgAmIc@iDyA{KMy@S}AGBE?A?Ga@AMC@E@cCh@SmAGGeAVOB_ACaAHYHuDbAo@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[UNCFCDEWe@wDg@wDS}ACWQFs@mFo@eFEWWLQCKIUYGMCKWHEKEIOIIEKw@mOvE}@yGGc@m@aEz@WNEvFaBnGgBjA]rBs@hA]|LuDvDoA`@QNGTx@ZdAJVt@jC~@bD~BfGvCxHN\\b@h@~@rAZr@Pl@Nt@^bDFl@t@WR`BfA[f@Og@NgAZSaBu@V[DOC}Bz@gBl@q@TuEvAoEvAeHvBqBp@DXB@l@Md@xDNnAaAHm@NaD|@o@?",
            "u|liGjqocNn@?`D}@l@O`AIOoAS_BQyAg@LG?G[bF_BtHaCtImCfBm@|B{@NBZEt@Wn@pFb@~DPvAB?XIFj@@@B?^KDLhBnNBNB@`Be@LF`@f@Z\\Fh@XvBJz@?^BFPb@^h@Hd@BJBDb@H`@FRHTLL@HJHRBCLCBC`@o@f@v@`@f@@JlAhB^r@^bAHb@H|@?FLADASB@^?x@i@vGYzCEfA@d@V|BDXE@mBh@eCz@{GvBw@ZwAb@qCv@_Cp@MDBVmA\\lA]CWcBf@q@V{Ad@a@NcEnAeBh@cAXgBuNKw@WmBmAmJ{@uGqGrBeIfCuC~@^jCBB`@Ma@LCC_@kCtC_AdIgCvDmAfA]Gi@e@oDo@_Fg@{DeA{HAINGWoBg@{DeAaIJQHGFCBVBBLA`@KHl@XbCPvAaAHYHuDbAo@?",
    };


    private RouteGenerator(Context context, final String gMapsApiKey) {
        this.context = context;
        this.gMapsApiKey = gMapsApiKey;

    }

    public static RouteGenerator create(Context context, final String gMapsApiKey) {
        return new RouteGenerator(context, gMapsApiKey);
    }

    public void generateRoute(final OnRouteReadyCallback callback, final LatLng start, int numPoints, double totalDistance, double rotation) {
        generateRoute(callback, start, numPoints, totalDistance, rotation, false);
    }

    public void generateRoute(final OnRouteReadyCallback callback, final LatLng start, int numPoints, double totalDistance, double rotation, boolean isMock) {
        if (isMock) {
            int randomIndex = new Random().nextInt(mockPaths.length);
            final List<LatLng> decodedPath = PolyUtil.decode(mockPaths[randomIndex]);

            // mock markers by uniformly distributed space
            List<LatLng> markers = new ArrayList<>();
            int skipInterval = decodedPath.size() / 5;

            for (int i = 0; i < decodedPath.size(); i += skipInterval ) {
                markers.add(decodedPath.get(i));
            }

            callback.onRouteReady(new Route(markers, mockPaths[randomIndex], decodedPath));
            return;
        }


        final List<LatLng> markers = generateWaypoints(start, numPoints, totalDistance, rotation);

        // Build waypoint string
        String waypointDelimiter = "";
        StringBuilder builder = new StringBuilder();
        for (final LatLng waypoint : markers.subList(1, markers.size())) {
            builder.append(waypointDelimiter);
            waypointDelimiter = "|";
            builder.append(waypoint.latitude)
                    .append(",")
                    .append(waypoint.longitude);
        }

        final String url = DIRECTIONS_ENDPOINT + "json?" +
                "origin=" + start.latitude + "," + start.longitude + "&"+
                "destination=" + start.latitude + "," + start.longitude + "&" +
                "waypoints=" + builder.toString() + "&" +
                "mode=walking&" +
                "avoid=tolls|highways|ferries&" +
                "key=" + gMapsApiKey;

        // Log.i(TAG, url);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.i(TAG, response.toString());
                try {
                    final String encodedPath = response.getJSONArray("routes")
                            .getJSONObject(0)
                            .getJSONObject("overview_polyline")
                            .getString("points");

                    Log.i(TAG, encodedPath);

                    final List<LatLng> decodedPath = PolyUtil.decode(encodedPath);

                    callback.onRouteReady(new Route(markers, encodedPath, decodedPath));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private static List<LatLng> generateWaypoints(final LatLng start, int numPoints, double totalDistance, double rotation) {
        if (numPoints < 3) {
            throw new IllegalArgumentException(String.format("numPoints needs to be at least 3, %s is given", numPoints));
        }
        final double l = totalDistance / numPoints;
        final double a = 2 * Math.PI / numPoints;
        final double r = l / Math.sqrt(2 * (1 - Math.cos(a)));

        List<LatLng> result = new ArrayList<>(numPoints);

        for (int i = 0; i < numPoints; ++i) {
            final double b = i * a + rotation;
            result.add(new LatLng(
                    r * (Math.cos(b) - Math.cos(rotation)) + start.latitude,
                    r * (Math.sin(b) - Math.sin(rotation)) + start.longitude
            ));
        }
        return result;
    }
}
