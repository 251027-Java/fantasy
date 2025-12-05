/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all assist/source/organizeImports: whatever */
/** biome-ignore-all lint/suspicious/noExplicitAny: any is fine */
import { HttpClient } from '@angular/common/http';
import { Injectable, signal, WritableSignal } from '@angular/core';
import { map, Observable } from 'rxjs';
import { StatsResponse } from '../interface/stats-response';

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  readonly numDecimalPlaces: number = 10

  constructor(private http:HttpClient) {}

  luckStatsResponse: WritableSignal<StatsResponse> = signal({stats:[]});

  currentLeagueId: string = "1252005113573150720";
  currentLeagueName: string = "Rice League";

  getLeagueLuckStats():void{
    let resp: Observable<StatsResponse> = this.http.get<StatsResponse>(`api/league/${this.currentLeagueId}/stats`)
    resp = resp.pipe(
      map<any, StatsResponse>(data => {
        const resp: StatsResponse = {stats:[]}
        for (const stat of data.stats){
          resp.stats.push({
            name: stat.userName,
            scores: {
              totalLuck: Number(stat.score.totalLuck.toFixed(this.numDecimalPlaces)),
              medLuck: Number(stat.score.medLuck.toFixed(this.numDecimalPlaces)),
              apLuck: Number(stat.score.apLuck.toFixed(this.numDecimalPlaces)),
              apWins: stat.score.apWins,
              apLoses: stat.score.apLoses,
              apTies: stat.score.apTies,
              wins: stat.score.wins,
              loses: stat.score.loses,
              ties: stat.score.ties
            }
          })
        }

        return resp;
      })
    )
    
    resp.subscribe(data => {
      this.luckStatsResponse.set(data)
    })

  }

}
