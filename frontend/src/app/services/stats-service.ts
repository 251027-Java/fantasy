/** biome-ignore-all lint/style/useImportType: idk */
/** biome-ignore-all assist/source/organizeImports: whatever */
/** biome-ignore-all lint/suspicious/noExplicitAny: any is fine */
import { HttpClient } from '@angular/common/http';
import { Injectable, signal, WritableSignal } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Score, StatsResponse } from '../interface/stats-response';
import { luckStatColumn } from '../components/luck-scores/luck-scores';

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  readonly numDecimalPlaces: number = 10

  // Inject HttpClient for making HTTP requests
  constructor(private http:HttpClient) {}

  luckStatsResponse: WritableSignal<StatsResponse> = signal({stats:[]});

  // Current league info is hardcoded for now 
  currentLeagueId: string = "1252005113573150720";
  currentLeagueName: string = "Rice League";

  private columnWidthClassStrings: string[] = ["w-1/3", "w-1/4", "w-1/5", "w-1/6", "w-1/7", "w-1/8", "w-1/9", "w-1/10", "w-1/11", "w-1/12", "w-1/12"]

  // Get's the league luck stats from the getLeagueStats function endpoint in the backend
  getLeagueLuckStats():void{

    // Recieve an observable after a get request to the spring backend. Map LeagueStatsDTO to stats-response interface
    let resp: Observable<StatsResponse> = this.http.get<StatsResponse>(`api/league/${this.currentLeagueId}/stats`)
    
    // Pipe the data to map it to the StatsResponse interface
    resp = resp.pipe(
      map<any, StatsResponse>(data => {

        // Map the received data to the StatsResponse interface
        const resp: StatsResponse = {stats:[]}
        for (const stat of data.stats){

          // Push each stat into the stats array with proper formatting
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

        // Return the resp object containing the mapped stats
        return resp;
      })
    )
    
    // Subscribe to the observable and set the luckStatsResponse signal with the received data
    resp.subscribe(data => {
      this.luckStatsResponse.set(data)
    })

  }

  getColumnWidthClassString(numStatColumnWidths: number): string{
    return this.columnWidthClassStrings[numStatColumnWidths - 1];
  }

  sortLuckStats(column: luckStatColumn, sortAsc: boolean) {
    if (column === ('name' as luckStatColumn)) {
      this.luckStatsResponse().stats.sort((a, b) => a.name.localeCompare(b.name) * (sortAsc ? 1 : -1));
    }

    this.luckStatsResponse().stats.sort((a, b) => {
      if (a.scores[column as keyof Score] < b.scores[column as keyof Score]) return sortAsc ? -1 : 1;
      if (a.scores[column as keyof Score] > b.scores[column as keyof Score]) return sortAsc ? 1 : -1;
      return 0;
    });
  }

  // Adding setters and getters to stats-service to store current league info
  setCurrentLeagueId(leagueId: string): void {
    this.currentLeagueId = leagueId;
  }

  getCurrentLeagueId(): string {
    return this.currentLeagueId;
  }

  setCurrentLeagueName(leagueName: string): void {
    this.currentLeagueName = leagueName;
  }

  getCurrentLeagueName(): string {
    return this.currentLeagueName;
  }

}
